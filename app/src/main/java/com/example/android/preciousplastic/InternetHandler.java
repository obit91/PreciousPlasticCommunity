//package com.example.android.preciousplastic;
//
//import android.content.Context;
//import android.util.Log;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//import com.google.gson.Gson;
//import com.google.gson.internal.LinkedTreeMap;
//
//import java.util.ArrayList;
//
//class InternetHandler {
//
//    final static String GET_MAP_PINS = "Get Map Pins";
//
//    // url addresses
//    private final String BASE_URL = "https://davehakkens.nl";
//    private final String MAP_PINS_SUFFIX = "/wp-json/map/v1/pins";
//
//    // keys of pins on map
//    private final class MapPinKeys{
//        final static String ID = "ID";
//        final static String NAME = "name";
//        final static String LAT = "lat";
//        final static String LNG = "lng";
//        final static String DESC = "description";
//        final static String SITE = "website";
//        final static String FILTERS = "filters";
//        final static String IMGS = "imgs";
//        final static String STATUS = "status";
//        final static String CREATED = "created_date";
//        final static String MODIFIED = "modified_date";
//        final static String USERNAME = "username";
//    }
//
//
//    private Context context;
//    public MainActivity serverResponseDelegate;
//    RequestQueue queue;
//
//    InternetHandler(Context context, MainActivity delegate){
//        this.context = context;
//        serverResponseDelegate = delegate;
//
//        // Instantiate the RequestQueue
//        queue = Volley.newRequestQueue(context);
//
//    }
//
//    /**
//     * Request from web API all pins from PreciousPlastic Map.
//     */
//    void getMapPins(){
//
//        String pinsUrl = BASE_URL + MAP_PINS_SUFFIX;
//        StringRequest dummyRequest = new StringRequest(Request.Method.GET, pinsUrl, new Response.Listener<String>(){
//            @Override
//            public void onResponse(String response){
//                Gson gson = new Gson();
//                try{
//                    ArrayList arrayList = gson.fromJson(response, ArrayList.class);
//                    for (Object entry: arrayList){
//                        LinkedTreeMap<String, String> linkedTreeMap = (LinkedTreeMap<String, String>) entry;
//                        // example usage
//                        String name = linkedTreeMap.get(MapPinKeys.NAME);
//                        serverResponseDelegate.onServerResponse(GET_MAP_PINS, name);
//                    }
//                } catch (Exception e){
//                    Log.e("exception", e.toString());
//                }
//            }
//        }, new Response.ErrorListener(){
//            @Override
//            public void onErrorResponse(VolleyError error){
//                System.out.println("Error response: " + String.valueOf(error));
//            }
//        });
//        // Add the request to queue
//        queue.add(dummyRequest);
//    }
//
//}
