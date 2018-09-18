package com.example.android.preciousplastic.utils;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;

public class InternetQuery {

    // url addresses
    private final static String BASE_URL = "https://davehakkens.nl";
    private final static String MAP_PINS_SUFFIX = "/wp-json/map/v1/pins";

    // manage requests from internet
    private static RequestQueue internetRequestsQueue = Volley.newRequestQueue(PPSession.getContainerContext());

    // list of pins as will be pulled from web.
    private static ArrayList webPinsArrayList;

    /**
     * Request from website map all available pins.
     * @param eventNotifier will be notified when query results are available.
     */
    public static void queryPins(final EventNotifier eventNotifier){
        String pinsUrl = BASE_URL + MAP_PINS_SUFFIX;
        StringRequest pinKeyRequest = new StringRequest(Request.Method.GET, pinsUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                try {
                    webPinsArrayList = gson.fromJson(response, ArrayList.class);
                    eventNotifier.onResponse(webPinsArrayList);
                } catch (Exception e) {
                    Log.e("gson exception", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                eventNotifier.onError(error.toString());
            }
        });
        internetRequestsQueue.add(pinKeyRequest);
    }
}
