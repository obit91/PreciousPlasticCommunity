package com.example.android.preciousplastic.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Toast;

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
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;

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
        final static String FILTERS = "filters";
        final static String IMGS = "imgs";
        final static String STATUS = "status";
        final static String CREATED = "created_date";
        final static String MODIFIED = "modified_date";
        final static String USERNAME = "username";
    }

    private Context context;
    private RequestQueue requestQueue;
    private MapView mapView;

    // pop up window when clicking on a map pin
    PopupWindow popupWindow;


    public MapActivity(Context context, HomeActivity delegate) {

        this.context = context;

        // TODO: handle OSMDROID dangerous permissions

        // ensure map has a writable location for the map cache
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        // Instantiate the RequestQueue (for internet requests)
        requestQueue = Volley.newRequestQueue(context);

    }

    /**
     * Define MapView, setup settings, and place pins on map.
     *
     * @param mapView
     */
    public void buildMap(MapView mapView) {
        this.mapView = mapView;

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

        // place pins on map
        setMapPins();

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

    /**
     * Request from web API all pins from PreciousPlastic Map.
     * Set each pin on MapView.
     */
    void setMapPins() {

        String pinsUrl = BASE_URL + MAP_PINS_SUFFIX;
        StringRequest pinKeyRequest = new StringRequest(Request.Method.GET, pinsUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                try {
                    ArrayList arrayList = gson.fromJson(response, ArrayList.class);
                    handlePinKeys(arrayList);
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
     * Place new Map Pin Keys on the Map View.
     *
     * @param pinsArrayList holds linkedTreeMaps, each representing a pin key for map
     */
    private void handlePinKeys(final ArrayList pinsArrayList) {

        // create OverlayItem per each Pin Key
        List<OverlayItem> points = new ArrayList<>();
        for (Object entry : pinsArrayList) {
            LinkedTreeMap<String, Object> linkedTreeMap = (LinkedTreeMap<String, Object>) entry;
            String name = (String) linkedTreeMap.get(MapPinKeys.NAME);
            double lat = (double) linkedTreeMap.get(MapPinKeys.LAT);
            double lng = (double) linkedTreeMap.get(MapPinKeys.LNG);
            OverlayItem tmpOverlayItem = new OverlayItem(name, "desc!", new GeoPoint(lat, lng));
            points.add(tmpOverlayItem);
        }

        // icon for workshops
        BitmapFactory.Options opt =  new BitmapFactory.Options();
        opt.inSampleSize = 1;
        Bitmap bitmap = BitmapFactory.decodeResource(PPSession.getContainerContext().getResources(), R.drawable.precious_plastic_logo_small, opt);
        Drawable drawable = new BitmapDrawable(PPSession.getContainerContext().getResources(), bitmap);

        // workshop pins overlay
        ItemizedIconOverlay<OverlayItem> mOverlay = new ItemizedIconOverlay<>(points, drawable,
            new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                @Override
                public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                    LinkedTreeMap<String, Object> chosenPointInfo = (LinkedTreeMap<String, Object>) pinsArrayList.get(index);

                    // Initialize a new instance of LayoutInflater service and inflate the popupWindow layout
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupView = inflater.inflate(R.layout.lo_map_pin_popup, null);

                    // set attributes to popup view
                    TextView title = popupView.findViewById(R.id.workshopTitle);
                    title.setText(item.getTitle());
                    TextView desc = popupView.findViewById(R.id.workshopDescription);
                    desc.setText((String) chosenPointInfo.get(MapPinKeys.DESC));
                    Button website = popupView.findViewById(R.id.websiteBtn);
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
                    return true;
                }
                @Override
                public boolean onItemLongPress(final int index, final OverlayItem item) {
                    return false;
                }
            }, PPSession.getContainerContext());

        // set the overlay on the map
        mapView.getOverlays().add(mOverlay);
    }

}

