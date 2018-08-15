package com.example.android.preciousplastic.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.utils.PPSession;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MapActivity extends AppCompatActivity {

    // url addresses
    private final String BASE_URL = "https://davehakkens.nl";
    private final String MAP_PINS_SUFFIX = "/wp-json/map/v1/pins";

    // logging tag
    private static final String TAG = "MAP_ACTIVITY";

    // keys of pins on map
    private final class MapPinKeys {
        final static String ID = "ID";
        final static String NAME = "name";
        final static String LAT = "lat";
        final static String LNG = "lng";
        final static String DESC = "description";
        final static String SITE = "website";
        final static String IMGS = "imgs";
        final static String STATUS = "status";
        final static String CREATED = "created_date";
        final static String MODIFIED = "modified_date";
        final static String USERNAME = "username";
        final static String FILTERS = "filters";
        final static String FILTERS_STARTED = "STARTED";    // Want to get started
        final static String FILTERS_WORKSHOP = "WORKSHOP";  // Workspace
        final static String FILTERS_MACHINE = "MACHINE";    // Machine Builder
    }

    // grid density binning
    private final static int DENSITY_X = 10;
    private final static int DENSITY_Y = 10;

    // pin can be drawn as single, or as a group icon
    enum PinType {SINGLE, GROUP}
    enum PinFilter {STARTED, WORKSHOP, MACHINE}

    private Context context;
    private RequestQueue requestQueue;
    private MapView mapView;

    // Overlays containing pins for map
    // Map Keys: WORKSHOP / MACHINE / STARTED --> Overlay
    Map<PinFilter, ItemizedIconOverlay<OverlayItem>> overlayMap;

    // 2d grids containing overlay items, used for clustering pins
    // Map Keys: WORKSHOP / MACHINE / STARTED --> 2d grid list
    Map<PinFilter, List<List<List<OverlayItem>>>> gridMap;

    // Key: WORKSHOP/STARATED/MACHINE, Value: List of all points
    Map<PinFilter, List<OverlayItem>> allPoints;

    // list of pins as will be pulled from web.
    ArrayList pinsArrayList;

    // pop up window when clicking on a map pin
    PopupWindow popupWindow;

    // current zoom level (rounded) in map
    int zoomLevel = 0;

    public MapActivity() {

        this.context = PPSession.getContainerContext();

        // TODO: handle OSMDROID dangerous permissions

        // ensure map has a writable location for the map cache
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        // Instantiate the RequestQueue (for internet requests)
        requestQueue = Volley.newRequestQueue(context);

        // prepare overlays for map
        // TODO: put in async
        initOverlaysGrid();
        initOverlays();
    }

    public void buildMap(MapView mapView){

        this.mapView = mapView;

        zoomLevel = (int) mapView.getZoomLevelDouble();

        // Map settings
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setHorizontalMapRepetitionEnabled(false);

        // set the map to a default starting point
        IMapController mapController = mapView.getController();
        // TODO: set to user's own location
        mapController.setZoom(9.0);
        GeoPoint startPoint = new GeoPoint(48.0, 4.0);
        mapController.setCenter(startPoint);

        // set map's onZoom
        mapView.addMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                MapActivity.this.onZoom();
                return false;
            }
        });

        // set and draw overlays on map
        setOverlays();
        drawOverlaysOnMap();
    }

    private void drawOverlaysOnMap(){
        mapView.getOverlays().clear();
        for (ItemizedIconOverlay<OverlayItem> mOverlay: overlayMap.values()){
            mapView.getOverlays().add(mOverlay);
        }
    }

    private void onZoom(){
        int newZoom = (int) mapView.getZoomLevelDouble();
        // process only if zoom jump is significant and repeats itself twice (user halts on zoom)
        if (newZoom != zoomLevel){
            zoomLevel = newZoom;
            setOverlays();
            drawOverlaysOnMap();
        }
    }

    public void onResume() {
        super.onResume();
        // refresh osmdroid configuration on resuming,
        // if changes are made to configuration, load them here
        mapView.onResume();
    }

    public void onPause() {
        super.onPause();
        // if changes are made to configuration, save them here
        mapView.onPause();
    }

    private void setOverlays(){

        // initialize containers
        initOverlaysGrid();
        overlayMap = new TreeMap<>();

        for (PinFilter pinFilter : gridMap.keySet()) {

            List<OverlayItem> overlayItemList = allPoints.get(pinFilter);
            List<List<List<OverlayItem>>> grid = gridMap.get(pinFilter);

            // populate grids with OverlayItems
            for (int j = 0; j < overlayItemList.size(); j++) {
                int binX;
                int binY;

                OverlayItem overlayItem = overlayItemList.get(j);
                Projection proj = mapView.getProjection();
                Point p = proj.toPixels(overlayItem.getPoint(), null);

                if (isWithin(p, mapView)) {
                    double fractionX = ((double) p.x / (double) mapView.getWidth());
                    binX = (int) (Math.floor(DENSITY_X * fractionX));
                    double fractionY = ((double) p.y / (double) mapView.getHeight());
                    binY = (int) (Math
                            .floor(DENSITY_Y * fractionY));
                    grid.get(binX).get(binY).add(overlayItem); // just push the reference
                }else{
                    Rect drawingRect = new Rect();
                    Point currDevicePos = new Point();

                    mapView.getDrawingRect(drawingRect);
                    Boolean within = drawingRect.contains(p.x, p.y);
                    Log.d("not isWithin", String.valueOf(within));
                }
            }
            // drawing
            for (int k = 0; k < DENSITY_X; k++) {
                for (int l = 0; l < DENSITY_Y; l++) {
                    List<OverlayItem> gridOverlayItemList = grid.get(k).get(l);
                    if (gridOverlayItemList.size() > 1) {
                        addOverlay(pinFilter, PinType.GROUP, gridOverlayItemList);
                    } else if (gridOverlayItemList.size() == 1) {
                        // draw single pin
                        addOverlay(pinFilter, PinType.SINGLE, gridOverlayItemList);
                    }
                }
            }
        }
    }

    public static boolean isWithin(Point p, MapView mapView) {
        return (p.x > 0 & p.x < mapView.getWidth() & p.y > 0 & p.y < mapView.getHeight());
    }

    /**
     * Request from web API all pins from PreciousPlastic Map.
     * Set each pin in its overlay.
     */
    private void initOverlays() {

        String pinsUrl = BASE_URL + MAP_PINS_SUFFIX;
        StringRequest pinKeyRequest = new StringRequest(Request.Method.GET, pinsUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                try {
                    pinsArrayList = gson.fromJson(response, ArrayList.class);
                    handlePinKeys();
                } catch (Exception e) {
                    Log.e("exception", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error response: " + String.valueOf(error));
            }
        });
        // Add the request to requestQueue
        requestQueue.add(pinKeyRequest);
    }

    /**
     * create grid to use for clustering, 2D array with some configurable, fixed density
     */
    private void initOverlaysGrid(){
        gridMap = new TreeMap<>();
        for (PinFilter pinFilter: PinFilter.values()){
            gridMap.put(pinFilter, new ArrayList<List<List<OverlayItem>>>(DENSITY_X));
            for (int i = 0; i < DENSITY_X; i++) {
                ArrayList<List<OverlayItem>> column = new ArrayList<>(DENSITY_Y);
                for (int j = 0; j < DENSITY_Y; j++) {
                    column.add(new ArrayList<OverlayItem>());
                }
                gridMap.get(pinFilter).add(column);
            }
        }
    }

    /**
     * Place new Map Pin Keys in their containers and overlays.
     */
    private void handlePinKeys() {

        // initialize allPoints container
        allPoints = new TreeMap<>();
        for (PinFilter pinFilter: PinFilter.values()){
            allPoints.put(pinFilter, new ArrayList<OverlayItem>());
        }

        // create OverlayItem per each Pin Key, and set to allPoints container
        for (Object entry : pinsArrayList) {
            LinkedTreeMap<String, Object> linkedTreeMap = (LinkedTreeMap<String, Object>) entry;
            String name = (String) linkedTreeMap.get(MapPinKeys.NAME);
            double lat = (double) linkedTreeMap.get(MapPinKeys.LAT);
            double lng = (double) linkedTreeMap.get(MapPinKeys.LNG);
            OverlayItem tmpOverlayItem = new OverlayItem(name, "desc!", new GeoPoint(lat, lng));

            // separate between STARTED / MACHINE / WORKSHOPS
            ArrayList<String> filters = (ArrayList<String>) linkedTreeMap.get(MapPinKeys.FILTERS);
            switch (filters.get(0)){
                case MapPinKeys.FILTERS_WORKSHOP:
                    allPoints.get(PinFilter.WORKSHOP).add(tmpOverlayItem);
                case MapPinKeys.FILTERS_STARTED:
                    allPoints.get(PinFilter.STARTED).add(tmpOverlayItem);
                case MapPinKeys.FILTERS_MACHINE:
                    allPoints.get(PinFilter.MACHINE).add(tmpOverlayItem);
            }
        }
    }

    /**
     * Will create an overlay for all given points.
     * @param overlayType WORKSHOP / STARTED / MACHINE
     * @param pinType icon should be SINGLE / GROUP
     * @param points all items to add to overlay
     */
    private void addOverlay(PinFilter overlayType, PinType pinType, List<OverlayItem> points){

        // bitmap options
        BitmapFactory.Options opt =  new BitmapFactory.Options();
        opt.inSampleSize = 1;

        // create bitmap
        Bitmap bitmap;
        switch (pinType) {
            case SINGLE:
                switch (overlayType) {
                    case WORKSHOP:
                        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.precious_plastic_logo_small, opt);
                        break;
                    case MACHINE:
                        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.machine_logo_small, opt);
                        break;
                    case STARTED:
                        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.started_logo_small, opt);
                        break;
                    default:
                        return;
                }
                break;
            case GROUP:
                switch (overlayType) {
                    case WORKSHOP:
                        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_background, opt);
                        break;
                    case MACHINE:
                        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.grey_background, opt);
                        break;
                    case STARTED:
                        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.yellow_background, opt);
                        break;
                    default:
                        return;
                }
                break;
            default:
                return;
        }
        Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
        ItemizedIconOverlay<OverlayItem> mOverlay = getOverlay(points, drawable);
        overlayMap.put(overlayType, mOverlay);
    }

    private void showPinPopUp(String title, String desc, String website){

        // Initialize a new instance of LayoutInflater service and inflate the popupWindow layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.lo_map_pin_popup, null);

        // set attributes to popup view
        TextView titleTxt = popupView.findViewById(R.id.workshopTitle);
        titleTxt.setText(title);
        TextView descTxt = popupView.findViewById(R.id.workshopDescription);
        descTxt.setText(desc);
        Button websiteBtn = popupView.findViewById(R.id.websiteBtn);
        //todo: open browser at website address

        // dismiss popup window if there is already one open
        if (popupWindow != null) {
            popupWindow.dismiss();
        }

        // Initialize a new popupWindow window
        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Get a reference for the custom view close button
        ImageButton closeButton = popupView.findViewById(R.id.closePopupBtn);

        // Set a click listener for the popup window close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                popupWindow.dismiss();
            }
        });
        // set location of popup window on screen to the clicked point
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    private ItemizedIconOverlay<OverlayItem> getOverlay(List<OverlayItem> points, Drawable icon){
        ItemizedIconOverlay<OverlayItem> mOverlay = new ItemizedIconOverlay<>(points, icon,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        LinkedTreeMap<String, Object> chosenPointInfo = (LinkedTreeMap<String, Object>) pinsArrayList.get(index);
                        // TODO: store description in overlayItem instead of having to query pointListArray (if possible)
                        String desc = (String) chosenPointInfo.get(MapPinKeys.DESC);
                        showPinPopUp(item.getTitle(), desc, "website");
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                }, context);
        return mOverlay;
    }

}

