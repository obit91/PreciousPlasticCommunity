package com.example.android.preciousplastic.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.db.repositories.HazardRepository;
import com.example.android.preciousplastic.utils.PPSession;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MapActivity extends AppCompatActivity {


    private Context context;
    private Resources resources;
    private RequestQueue requestQueue;
    private MapView mapView;

    // Overlays containing pins for map
    private List<ItemizedIconOverlay<OverlayItem>> overlayList;

    // 2d grids containing overlay items, used for clustering pins
    // Map Keys: WORKSPACE / MACHINE / STARTED --> 2d grid list
    private Map<MapConstants.PinFilter, List<List<List<OverlayItem>>>> gridMap;

    // Key: WORKSPACE/STARTED/MACHINE, Value: List of all points
    private Map<MapConstants.PinFilter, List<OverlayItem>> allPoints;

    // list of pins as will be pulled from web.
    private ArrayList pinsArrayList;

    // pop up window when clicking on a map pin
    private PopupWindow popupWindow;

    // pop up window for filter options & hazard reporting
    private PopupWindow filterWindow;

    // current zoom level (rounded) in map and View location coordinates
    int zoomLevel = 0;
    int x = 0;
    int y = 0;

    // overlay with long click listener
    private MapEventsOverlay OverlayEvents;

    // map filters which to be shown on map
    private Map<Integer, Boolean> filtersActivated;

    private HazardRepository hazardRepository;

    // ===========================================================
    // Constructors
    // ===========================================================

    public MapActivity() {

        context = PPSession.getContainerContext();
        resources = PPSession.getHomeActivity().getResources();
        hazardRepository = new HazardRepository(context);

        // TODO: handle OSMDROID dangerous permissions

        // ensure map has a writable location for the map cache
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        // Instantiate the RequestQueue (for internet requests)
        requestQueue = Volley.newRequestQueue(context);

        // initialize filters mapping
        filtersActivated = new TreeMap<>();
        filtersActivated.put(R.id.filter_workspace_checkbox, true);
        filtersActivated.put(R.id.filter_started_checkbox, true);
        filtersActivated.put(R.id.filter_machine_checkbox, true);
        filtersActivated.put(R.id.filter_hazard_checkbox, true);

        // prepare overlays for map
        // TODO: put in async
        initOverlaysGrid();
        initOverlays();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

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
        GeoPoint startPoint = new GeoPoint(30.0, 30.0);
        mapController.setCenter(startPoint);

        // add Image Button which opens a filter window
        setFilterListeners();

        // set map's onZoom
        mapView.addMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                MapActivity.this.onScroll(event);
                return true;
            }
            @Override
            public boolean onZoom(ZoomEvent event) {
                MapActivity.this.onZoom(event);
                return true;
            }
        });

        // set map's LongClickListener
        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                onLongClick(p);
                return true;
            }
        };
        OverlayEvents = new MapEventsOverlay(context, mReceive);
        mapView.getOverlays().add(OverlayEvents);

        // set and draw overlays on map
        setOverlays();
        drawOverlaysOnMap();
    }

    public void onLongClick(GeoPoint p){
        reportHazard(p);
    }

    public static boolean isWithin(Point p, MapView mapView) {
        return (p.x > 0 & p.x < mapView.getWidth() & p.y > 0 & p.y < mapView.getHeight());
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

    public void onScroll(ScrollEvent scrollEvent){
        int newX = scrollEvent.getX();
        int newY = scrollEvent.getY();
        // process only if movement is significant
        if (Math.hypot(newX-x, newY-y) > 100){
            onMove();
        }
    }

    public void onZoom(ZoomEvent zoomEvent){
        int newZoom = (int) zoomEvent.getZoomLevel();
        // process only if zoom jump is significant and repeats itself twice (user halts on zoom)
        if (newZoom != zoomLevel){
            zoomLevel = newZoom;
            onMove();
        }
    }

    /**
     * Something changed on screen, update map pins.
     */
    public void onMove(){
        setOverlays();
        drawOverlaysOnMap();
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * Set OnClickListener to open a window with filter options.
     */
    private void setFilterListeners(){
        FrameLayout mapLayout = (FrameLayout) PPSession.getHomeActivity().findViewById(R.id.fragment_map);
        ImageButton imageButton = (ImageButton) mapLayout.getChildAt(1);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filterWindow == null) {

                    // open filters popup window
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                    LinearLayout filterView = (LinearLayout) inflater.inflate(R.layout.lo_map_filter, null);
                    filterWindow = new PopupWindow(filterView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    filterWindow.showAtLocation(filterView, Gravity.CENTER, 0, 0);

                    // connect listeners to checkboxes
                    for (int i =0; i < filterView.getChildCount(); i++){
                        RelativeLayout singleCheckboxLayout = (RelativeLayout) filterView.getChildAt(i);
                        final CheckBox checkbox = (CheckBox) singleCheckboxLayout.getChildAt(0);
                        checkbox.setChecked(filtersActivated.get(checkbox.getId()));
                        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                filtersActivated.put(checkbox.getId(), b);
                                mapView.invalidate();
                                // TODO: not updating (neither is postInvalidate). mapView not refreshing.
                            }
                        });
                    }
                } else {
                    filterWindow.dismiss();
                    filterWindow = null;
                }
            }
        });
    }

    private void drawOverlaysOnMap(){
        mapView.getOverlays().clear();
        mapView.getOverlays().add(OverlayEvents);
        for (ItemizedIconOverlay<OverlayItem> mOverlay: overlayList){
            mapView.getOverlays().add(mOverlay);
        }
    }

    /**
     * Fill the clustering grid with the Overlay Items.
     * Create the relevant overlays and store them in a container.
     */
    private void setOverlays(){

        // initialize containers
        initOverlaysGrid();
        overlayList = new ArrayList<>();

        for (MapConstants.PinFilter pinFilter : gridMap.keySet()) {
            if (!(filtersActivated.get(MapConstants.filterConstsMap.get(pinFilter)))){
                continue;
            }
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
                    binX = (int) (Math.floor(MapConstants.DENSITY_X * fractionX));
                    double fractionY = ((double) p.y / (double) mapView.getHeight());
                    binY = (int) (Math.floor(MapConstants.DENSITY_Y * fractionY));
                    grid.get(binX).get(binY).add(overlayItem); // just push the reference
                }
            }
            // collect items from grid and assign to overlays
            List<OverlayItem> singleItems = new ArrayList<>();
            List<OverlayItem> groupItems = new ArrayList<>();
            for (int k = 0; k < MapConstants.DENSITY_X; k++) {
                for (int l = 0; l < MapConstants.DENSITY_Y; l++) {
                    List<OverlayItem> gridOverlayItemList = grid.get(k).get(l);
                    if (gridOverlayItemList.size() > 1) {
                        groupItems.addAll(gridOverlayItemList);
                    } else if (gridOverlayItemList.size() == 1) {
                        singleItems.add(gridOverlayItemList.get(0));
                    }
                }
            }
            addOverlay(pinFilter, MapConstants.PinType.SINGLE, singleItems);
            addOverlay(pinFilter, MapConstants.PinType.GROUP, groupItems);
        }
    }

    /**
     * Request from web API all pins from PreciousPlastic Map.
     * Set each pin in its overlay.
     */
    private void initOverlays() {

        String pinsUrl = MapConstants.BASE_URL + MapConstants.MAP_PINS_SUFFIX;
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
                Log.e("Error response", String.valueOf(error));
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
        for (MapConstants.PinFilter pinFilter: MapConstants.PinFilter.values()){
            gridMap.put(pinFilter, new ArrayList<List<List<OverlayItem>>>(MapConstants.DENSITY_X));
            for (int i = 0; i < MapConstants.DENSITY_X; i++) {
                ArrayList<List<OverlayItem>> column = new ArrayList<>(MapConstants.DENSITY_Y);
                for (int j = 0; j < MapConstants.DENSITY_Y; j++) {
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
        for (MapConstants.PinFilter pinFilter: MapConstants.PinFilter.values()){
            allPoints.put(pinFilter, new ArrayList<OverlayItem>());
        }

        // create OverlayItem per each Pin Key, and set to allPoints container
        for (Object entry : pinsArrayList) {
            LinkedTreeMap<String, Object> linkedTreeMap = (LinkedTreeMap<String, Object>) entry;
            String name = (String) linkedTreeMap.get(MapConstants.MapPinKeys.NAME);
            double lat = (double) linkedTreeMap.get(MapConstants.MapPinKeys.LAT);
            double lng = (double) linkedTreeMap.get(MapConstants.MapPinKeys.LNG);
            OverlayItem tmpOverlayItem = new OverlayItem(name, "desc!", new GeoPoint(lat, lng));

            // separate between WORKSPACES / MACHINE / STARTED
            // give precedence in this order, as some items may match several features
            // TODO: allow pin to have several filters?
            ArrayList<String> filters = (ArrayList<String>) linkedTreeMap.get(MapConstants.MapPinKeys.FILTERS);
            switch (filters.get(0)){
                case MapConstants.MapPinKeys.FILTERS_WORKSPACE:
                    allPoints.get(MapConstants.PinFilter.WORKSPACE).add(tmpOverlayItem);
                    break;
                case MapConstants.MapPinKeys.FILTERS_MACHINE:
                    allPoints.get(MapConstants.PinFilter.MACHINE).add(tmpOverlayItem);
                    break;
                case MapConstants.MapPinKeys.FILTERS_STARTED:
                    allPoints.get(MapConstants.PinFilter.STARTED).add(tmpOverlayItem);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Will create an overlay for all given points.
     * @param overlayType WORKSPACE / STARTED / MACHINE
     * @param pinType icon should be SINGLE / GROUP
     * @param points all items to add to overlay
     */
    private void addOverlay(MapConstants.PinFilter overlayType, MapConstants.PinType pinType, List<OverlayItem> points){

        // bitmap options
        BitmapFactory.Options opt =  new BitmapFactory.Options();
        opt.inSampleSize = 1;

        // create bitmap
        Drawable drawable;
        switch (pinType) {
            case SINGLE:
                switch (overlayType) {
                    case WORKSPACE:
                        drawable = context.getResources().getDrawable(R.drawable.precious_plastic_logo_small);
                        break;
                    case MACHINE:
                        drawable = context.getResources().getDrawable(R.drawable.ic_machine_pin_foreground);
                        break;
                    case STARTED:
                        drawable = context.getResources().getDrawable(R.drawable.ic_started_pin_foreground);
                        break;
                    case HAZARDS:
                        drawable = context.getResources().getDrawable(R.drawable.hazard_icon);
                        break;
                    default:
                        return;
                } break;
            case GROUP:
                switch (overlayType) {
                    case WORKSPACE:
                        drawable = context.getResources().getDrawable(R.drawable.blue_cluster);
                        break;
                    case MACHINE:
                        drawable = context.getResources().getDrawable(R.drawable.grey_cluster);
                        break;
                    case STARTED:
                        drawable = context.getResources().getDrawable(R.drawable.orange_cluster);
                        break;
                    case HAZARDS:
                        drawable = context.getResources().getDrawable(R.drawable.yellow_cluster);
                        break;
                    default:
                        return;
                } break;
            default:
                return;
        }
        ItemizedIconOverlay<OverlayItem> mOverlay = getOverlay(points, drawable, pinType);
        overlayList.add(mOverlay);
    }

    private void showPinPopUp(String title, String desc, final String website){

        // Initialize a new instance of LayoutInflater service and inflate the popupWindow layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.lo_map_pin_popup, null);

        // set attributes to popup view
        TextView titleTxt = popupView.findViewById(R.id.workspaceTitle);
        titleTxt.setText(title);
        TextView descTxt = popupView.findViewById(R.id.workspaceDescription);
        descTxt.setText(desc);
        Button websiteBtn = popupView.findViewById(R.id.websiteBtn);
        websiteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Log.i("Website OnClick", website);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                    context.startActivity(browserIntent);
                }catch (Exception e){
                    Log.e("Website OnClick error", e.toString());
                }
            }
        });

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

    private ItemizedIconOverlay<OverlayItem> getOverlay(List<OverlayItem> points, Drawable icon, final MapConstants.PinType pinType){
        ItemizedIconOverlay<OverlayItem> mOverlay = new ItemizedIconOverlay<>(points, icon,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        if (pinType == MapConstants.PinType.SINGLE) {
                            LinkedTreeMap<String, Object> chosenPointInfo = (LinkedTreeMap<String, Object>) pinsArrayList.get(index);
                            // TODO: store description in overlayItem instead of having to query pointListArray (if possible)
                            String desc = (String) chosenPointInfo.get(MapConstants.MapPinKeys.DESC);
                            showPinPopUp(item.getTitle(), desc, (String) chosenPointInfo.get(MapConstants.MapPinKeys.SITE));
                        }
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                }, context);
        return mOverlay;
    }

    private void reportHazard(GeoPoint p){
        Toast.makeText(context, "Where's the fire?!", Toast.LENGTH_SHORT).show();
        hazardRepository.insertHazard(PPSession.getFirebaseAuth().getCurrentUser(), p, "serious hazard");
    }
}

