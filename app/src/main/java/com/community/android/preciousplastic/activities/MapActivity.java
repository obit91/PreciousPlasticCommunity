package com.community.android.preciousplastic.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.community.android.preciousplastic.R;
import com.community.android.preciousplastic.fragments.FragmentMap;
import com.community.android.preciousplastic.utils.EventNotifier;
import com.community.android.preciousplastic.db.repositories.HazardRepository;
import com.community.android.preciousplastic.utils.InternetQuery;
import com.community.android.preciousplastic.utils.PPLocationManager;
import com.community.android.preciousplastic.utils.PPSession;
import com.community.android.preciousplastic.permissions.PermissionManager;
import com.google.firebase.database.DataSnapshot;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MapActivity extends BaseActivity {

    private static final String TAG = "MAP_ACTIVITY";

    private static final Double ZOOM_MIN_SCOPE = 3.0;

    /**
     * Hardcoded locations of map edges.
     */
    private static final Double NORTH = -85.0;
    private static final Double EAST = -180.0;
    private static final Double SOUTH = 85.0;
    private static final Double WEST = 180.0;

    private Context context;
    private Resources resources;
    private RequestQueue requestQueue;
    private MapView mapView;
    private final double INIT_ZOOM_VAL = 10.0;
    private double latLoc = 31.0461;
    private double longLoc = 34.8516;

    private FragmentMap fragmentMap = null;

    private PPLocationManager ppLocationManager;


    // Overlays containing pins for map
    private List<ItemizedIconOverlay<OverlayItem>> overlayList;

    // 2d grids containing overlay items, used for clustering pins
    // Map Keys: WORKSPACE / MACHINE / STARTED --> 2d grid list
    private Map<MapConstants.PinFilter, List<List<List<OverlayItem>>>> gridMap;

    // Key: WORKSPACE/STARTED/MACHINE, Value: List of all points
    private Map<MapConstants.PinFilter, List<OverlayItem>> allPoints;

    // list of pins as will be pulled from web.
    private ArrayList webPinsArrayList;

    // pop up window when clicking on a map pin
    private PopupWindow popupWindow;

    // pop up window for filter options & hazard reporting
    private PopupWindow filterWindow;

    // pop up window for reporting hazards
    private PopupWindow hazardWindow;

    // current zoom level (rounded) in map and View location coordinates
    int zoomLevel = 0;
    int x = 0;
    int y = 0;

    // overlay with long click listener
    private MapEventsOverlay OverlayEvents;

    // map filters which to be shown on map
    private Map<Integer, Boolean> filtersActivated;

    private HazardRepository hazardRepository;

    private boolean initialized = false;

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

        // initialize filters mapping
        filtersActivated = new TreeMap<>();
        filtersActivated.put(R.id.filter_workspace_checkbox, true);
        filtersActivated.put(R.id.filter_started_checkbox, true);
        filtersActivated.put(R.id.filter_machine_checkbox, true);
        filtersActivated.put(R.id.filter_hazard_checkbox, true);

        // prepare overlays for map
        new InitMapsDataAsyncTask().execute();
    }

    public void setMapFragment(FragmentMap fragmentMap) {
        this.fragmentMap = fragmentMap;
    }

    // ===========================================================
    // Public Methods & Classes
    // ===========================================================

    public void buildMap(MapView mapView){

        if (!initialized){ return; }

        this.mapView = mapView;

        mapView.setMinZoomLevel(ZOOM_MIN_SCOPE);

        zoomLevel = (int) mapView.getZoomLevelDouble();

        // Map settings
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setHorizontalMapRepetitionEnabled(true);
        mapView.setVerticalMapRepetitionEnabled(false);

        ppLocationManager = new PPLocationManager(context);
        // ask user to allow location in settings
        if (!ppLocationManager.canGetLocation()){
            getPermissions();
        } else {
            buildWithPermissions();
        }
    }

    public void removeFilters() {
        if (filterWindow != null && filterWindow.isShowing()) {
            filterWindow.dismiss();
            filterWindow = null;
        }
    }

    public void onLongClick(GeoPoint p){
        reportHazard(p);
    }

    public static boolean isWithin(Point p, MapView mapView) {
        return (p.x > 0 & p.x < mapView.getWidth() & p.y > 0 & p.y < mapView.getHeight());
    }

    private void getPermissions() {
        PermissionManager permissionManager = new PermissionManager(fragmentMap);
        permissionManager.showStatePermissions(PermissionManager.PERMISSIONS.REQUEST_PERMISSION_FINE_LOCATION);
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
        scrollEvent.getSource().getBoundingBox();
        if (Math.hypot(newX-x, newY-y) > 100){
            onMove();
        }
    }

    public void onZoom(ZoomEvent zoomEvent){
        int newZoom = (int) zoomEvent.getZoomLevel();
        // process only if zoom jump is significant and repeats itself twice (user halts on zoom)
        if (newZoom != zoomLevel){
            Log.d(TAG, "onZoom: zoom level changed to: " + newZoom);
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

    /**
     * Make keyboard disappear when user clicks on screen
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight()
                    || y < w.getTop() || y > w.getBottom()) ) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // ===========================================================
    // Private Classes
    // ===========================================================

    private class InitMapsDataAsyncTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... nulls){
            // TODO: restore
            initOverlays();
            return null;
        }
        @Override
        protected void onPostExecute(Void nulls){
            initialized = true;
        }
    }

    /**
     * Wait for query results from hazards db, and create map pin for each hazard.
     */
    private class HazardsQueryAllEventNotifier extends EventNotifier{
        @Override
        public void onResponse(Object dataSnapshotObj){
            DataSnapshot dataSnapshot;
            try {
                dataSnapshot = (DataSnapshot) dataSnapshotObj;
            } catch (ClassCastException e){
                onError(e.toString());
                return;
            }
            for (DataSnapshot singleHazard: dataSnapshot.getChildren()){
                Object hazardObject = singleHazard.getValue();
                HashMap<String, Object> hazardMap = (HashMap<String, Object>) hazardObject;
                String desc = (String) hazardMap.get("description");
                HashMap<String, Object> location = (HashMap<String, Object>) hazardMap.get("location");
                double lat = (double) location.get("latitude");
                double lng = (double) location.get("longitude");
                OverlayItem overlayItem = new OverlayItem("Hazard Treasure", desc, new GeoPoint(lat, lng));
                allPoints.get(MapConstants.PinFilter.HAZARDS).add(overlayItem);
            }
        }
    }

    /**
     * Wait on map pins query result from internet, and distribute map pins to containers when received.
     */
    private class MapPinsQueryEventNotifier extends EventNotifier{
        @Override
        public void onResponse(Object response){
            try {
                webPinsArrayList = (ArrayList) response;
                handlePinKeys();
            } catch (ClassCastException e){
                onError(e.toString());
            }
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * Set OnClickListener to open a window with filter options.
     */
    private void setFilterListeners(){
        FrameLayout mapLayout = (FrameLayout) PPSession.getHomeActivity().findViewById(R.id.fragment_map);
        ImageButton imageButton = mapLayout.findViewById(R.id.filter_btn);
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
                    removeFilters();
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
                    //todo: check if item at location already exists in bin, and if so, change its location by epsilon
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
     * Request from firebase db the hazard pins.
     * Set each pin in its overlay.
     */
    private void initOverlays() {

        initOverlaysGrid();

        // initialize allPoints container
        allPoints = new TreeMap<>();
        for (MapConstants.PinFilter pinFilter: MapConstants.PinFilter.values()){
            allPoints.put(pinFilter, new ArrayList<OverlayItem>());
        }
        // todo remove comment
        // request and handle pins from map
        InternetQuery.queryPins(new MapPinsQueryEventNotifier());

        // get also hazard items from DB
        hazardRepository.getHazards(new HazardsQueryAllEventNotifier());
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

        // create OverlayItem per each Pin Key, and set to allPoints container
        for (Object entry : webPinsArrayList) {
            LinkedTreeMap<String, Object> linkedTreeMap = (LinkedTreeMap<String, Object>) entry;
            String name = (String) linkedTreeMap.get(MapConstants.MapPinKeys.NAME);
            String desc = (String) linkedTreeMap.get(MapConstants.MapPinKeys.DESC);
            double lat = (double) linkedTreeMap.get(MapConstants.MapPinKeys.LAT);
            double lng = (double) linkedTreeMap.get(MapConstants.MapPinKeys.LNG);
            OverlayItem tmpOverlayItem = new OverlayItem(name, desc, new GeoPoint(lat, lng));

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
        ItemizedIconOverlay<OverlayItem> mOverlay = getOverlay(points, drawable, pinType, overlayType);
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
        if (!website.equals("")) {
            websiteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                        browserIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(browserIntent);
                    } catch (Exception e) {
                        Log.e("Website OnClick error", e.toString());
                    }
                }
            });
        } else {
            websiteBtn.setVisibility(View.GONE);
        }

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

    private ItemizedIconOverlay<OverlayItem> getOverlay(List<OverlayItem> points, Drawable icon, final MapConstants.PinType pinType, final MapConstants.PinFilter pinFilter){
        ItemizedIconOverlay<OverlayItem> mOverlay = new ItemizedIconOverlay<>(points, icon,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {

                        if (pinType == MapConstants.PinType.SINGLE) {
                            LinkedTreeMap<String, Object> chosenPointInfo = (LinkedTreeMap<String, Object>) webPinsArrayList.get(index);
                            String website = "";
                            if (pinFilter == MapConstants.PinFilter.WORKSPACE) {
                                // TODO: store website in overlayItem instead of having to query pointListArray (if possible)
                                website = (String) chosenPointInfo.get(MapConstants.MapPinKeys.SITE);
                            }
                            showPinPopUp(item.getTitle(), item.getSnippet(), website);
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

    private void reportHazard(final GeoPoint p){

        final FrameLayout hazardView = (FrameLayout) PPSession.getHomeActivity().findViewById(R.id.lo_report_hazard);
        if (hazardView.getVisibility() != View.VISIBLE) {
            hazardView.setVisibility(View.VISIBLE);

            // connect all buttons
            final EditText desc = hazardView.findViewById(R.id.hazard_desc);
            Button reportButton = hazardView.findViewById(R.id.submit_hazard_report);
            reportButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TextUtils.isEmpty(desc.getText())){
                        Toast.makeText(context, "Forgot some description?", Toast.LENGTH_SHORT).show();
                    } else {
                        hazardRepository.insertHazard(PPSession.getFirebaseAuth().getCurrentUser(), p, "serious hazard");
                        Toast.makeText(context, "Hazard reported", Toast.LENGTH_SHORT).show();
                        hazardView.setVisibility(View.GONE);
                    }
                }
            });
            ImageButton closeButton = hazardView.findViewById(R.id.close_hazard_btn);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hazardView.setVisibility(View.GONE);
                }
            });
        }
    }

    public void buildWithPermissions() {

        // set the map to user's location
        double initLat = latLoc;
        double initLong = longLoc;

        if (ppLocationManager.canGetLocation()){
            initLat = ppLocationManager.getLatitude();
            initLong = ppLocationManager.getLongitude();
        }
        ppLocationManager.stopUsingGPS();

        IMapController mapController = mapView.getController();
        mapController.setZoom(INIT_ZOOM_VAL);
        GeoPoint startPoint = new GeoPoint(initLat, initLong);
        mapController.setCenter(startPoint);

        // add Image Button which opens a filter window
        setFilterListeners();

        // set map's onZoom
        mapView.addMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                removeFilters();
                MapActivity.this.onScroll(event);
                return true;
            }
            @Override
            public boolean onZoom(ZoomEvent event) {
                removeFilters();
                MapActivity.this.onZoom(event);
                return true;
            }
        });

        // set map's LongClickListener
        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                removeFilters();
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                removeFilters();
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
}

