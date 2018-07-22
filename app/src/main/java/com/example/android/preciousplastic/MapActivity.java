package com.example.android.preciousplastic;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;

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

    MapActivity(Context context, MainActivity delegate){

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
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

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
                    for (Object entry: arrayList){
                        LinkedTreeMap<String, String> linkedTreeMap = (LinkedTreeMap<String, String>) entry;
                        handlePinKey(linkedTreeMap);
                    }
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
     * Place new Map Pin Key on the Map View.
     * @param linkedTreeMap keys are from MapPinKeys consts class
     */
    private void handlePinKey(LinkedTreeMap<String, String> linkedTreeMap){
        // example usage
        String name = linkedTreeMap.get(MapPinKeys.NAME);
        // place pin on mapView
    }

}
