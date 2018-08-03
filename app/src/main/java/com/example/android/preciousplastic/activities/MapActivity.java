package com.example.android.preciousplastic.activities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlayOptions;
import org.osmdroid.views.overlay.simplefastpoint.SimplePointTheme;

import java.util.ArrayList;
import java.util.List;

class MapActivity extends AppCompatActivity {

    // url addresses
    private final String BASE_URL = "https://davehakkens.nl";
    private final String MAP_PINS_SUFFIX = "/wp-json/map/v1/pins";

    // keys of pins on map
    private final class MapPinKeys{
        final static String ID = "ID";
        final static String NAME = "name";
        final static String LAT = "lat";
        final static String LNG = "lng";
        final static String DESC = "description";
        final static String SITE = "website";
        final static String FILTERS = "filters";
        final static String IMGS = "imgs";
        final static String STATUS = "status";
        final static String CREATED = "created_date";
        final static String MODIFIED = "modified_date";
        final static String USERNAME = "username";
    }

    private RequestQueue requestQueue;
    private MapView mapView;

    MapActivity(Context context, HomeActivity delegate){

        // TODO: handle OSMDROID dangerous permissions

        // ensure map has a writable location for the map cache
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        // Instantiate the RequestQueue (for internet requests)
        requestQueue = Volley.newRequestQueue(context);

    }

    /**
     * Define MapView, setup settings, and place pins on map.
     * @param mapView
     */
    public void buildMap(MapView mapView){
        this.mapView = mapView;

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

        // TODO get pins and place them on map
        setMapPins();
    }

    public void onResume(){
        super.onResume();
        // refresh osmdroid configuration on resuming,
        // if changes are made to configuration, load them here
        mapView.onResume();
    }

    public void onPause(){
        super.onPause();
        // if changes are made to configuration, save them here
        mapView.onPause();
    }

    /**
     * Request from web API all pins from PreciousPlastic Map.
     * Set each pin on MapView.
     */
    void setMapPins(){

        String pinsUrl = BASE_URL + MAP_PINS_SUFFIX;
        StringRequest pinKeyRequest = new StringRequest(Request.Method.GET, pinsUrl, new Response.Listener<String>(){
            @Override
            public void onResponse(String response){
                Gson gson = new Gson();
                try{
                    ArrayList arrayList = gson.fromJson(response, ArrayList.class);
                    handlePinKeys(arrayList);
                } catch (Exception e){
                    Log.e("exception", e.toString());
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                System.out.println("Error response: " + String.valueOf(error));
            }
        });
        // Add the request to requestQueue
        requestQueue.add(pinKeyRequest);
    }

    /**
     * Place new Map Pin Keys on the Map View.
     * @param arrayList holds linkedTreeMaps, each representing a pin key for map
     */
    private void handlePinKeys(ArrayList arrayList) {

        // create OverlayItem per each Pin Key
        List<IGeoPoint> points = new ArrayList<>();
        for (Object entry : arrayList) {
            LinkedTreeMap<String, Object> linkedTreeMap = (LinkedTreeMap<String, Object>) entry;
            String name = (String) linkedTreeMap.get(MapPinKeys.NAME);
            double lat = (double) linkedTreeMap.get(MapPinKeys.LAT);
            double lng = (double) linkedTreeMap.get(MapPinKeys.LNG);
            points.add(new LabelledGeoPoint(lat, lng, name));
        }
        // wrap points in a theme
        SimplePointTheme pointThm = new SimplePointTheme(points, true);

        // create label style
        // TODO replace this dummy style with pin images from 'res'
        Paint textStyle = new Paint();
        textStyle.setStyle(Paint.Style.FILL);
        textStyle.setColor(Color.parseColor("#0000ff"));
        textStyle.setTextAlign(Paint.Align.CENTER);
        textStyle.setTextSize(24);

        // set some visual options for the overlay
        // MEDIUM_OPTIMIZATION: do not use when > 10,000k points
        // 23/07/18 ~8,000k points
        SimpleFastPointOverlayOptions opts = SimpleFastPointOverlayOptions.getDefaultStyle()
                .setAlgorithm(SimpleFastPointOverlayOptions.RenderingAlgorithm.MEDIUM_OPTIMIZATION)
                .setRadius(7).setIsClickable(true).setCellSize(15).setTextStyle(textStyle);

        // create the overlay with the theme
        final SimpleFastPointOverlay fastOverlay = new SimpleFastPointOverlay(pointThm, opts);

        // onClick callback
        fastOverlay.setOnClickListener(new SimpleFastPointOverlay.OnClickListener() {
            @Override
            public void onClick(SimpleFastPointOverlay.PointAdapter points, Integer point) {
                // TODO pop up small window (create in separate layout)
                Toast.makeText(mapView.getContext()
                        , "You clicked " + ((LabelledGeoPoint) points.get(point)).getLabel()
                        , Toast.LENGTH_SHORT).show();
            }
        });
        mapView.getOverlays().add(fastOverlay);
    }

}

