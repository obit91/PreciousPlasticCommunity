package com.example.android.preciousplastic.activities;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.android.preciousplastic.utils.EventNotifier;
import com.example.android.preciousplastic.utils.InternetQuery;
import com.example.android.preciousplastic.utils.PPSession;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class WorkspacesActivity {

    private int radius = 4000;
    public boolean initialized = false;

    ArrayList<LinkedTreeMap<String, Object>> workspacesWithinRadius;
    ArrayList<Drawable> images;

    EventNotifier onInitCompleteEventNotifier;

    public WorkspacesActivity(){

        // get workspaces
        InternetQuery.queryPins(new WorkspacesQueryEventNotifier());
    }

    public ArrayList<LinkedTreeMap<String, Object>> getWorkspaces(){
        return workspacesWithinRadius;
    }

    public ArrayList<Drawable> getImages() {
        return images;
    }

    public void setRadius(int radius){
        this.radius = radius;
    }

    public void setEventNotifier(EventNotifier eventNotifier){
        onInitCompleteEventNotifier = eventNotifier;
    }

    private boolean withinUserRadius(double lat, double lng){
        // TODO get user's location
        double centerLng = 43;
        double centerLat = 43;
        float[] results = new float[1];
        Location.distanceBetween(centerLat, centerLng, lat, lng, results);
        float distanceInKiloMeters = results[0] / 1000;
        return distanceInKiloMeters < radius;
    }

    /**
     * Wait on map pins query result from internet.
     * Filter only WORKSPACE pins and those within set RADIUS from user.
     */
    private class WorkspacesQueryEventNotifier extends EventNotifier {
        @Override
        public void onResponse(Object response){
            try {
                Log.i("WorkspacesQuery", "response");
                new WorkspacesInitAsyncTask().execute((ArrayList) response);
            } catch (ClassCastException e){
                onError(e.toString());
            }
        }
    }

    private class WorkspacesInitAsyncTask extends AsyncTask<ArrayList, Void, Void>{

        ArrayList<String> img_urls;

        @Override
        protected Void doInBackground(ArrayList... pinsList){
            initWorkspaces(pinsList[0]);
            return null;
        }

        /**
         * Extract all workspaces and set them in view.
         * @param pinsList list of all pins on map (workspaces need to be filtered)
         */
        private void initWorkspaces(ArrayList pinsList){
            workspacesWithinRadius = new ArrayList<>();
            img_urls = new ArrayList<>();

            for (Object entry : pinsList) {
                LinkedTreeMap<String, Object> linkedTreeMap = (LinkedTreeMap<String, Object>) entry;
                ArrayList<String> filters = (ArrayList<String>) linkedTreeMap.get(MapConstants.MapPinKeys.FILTERS);
                if (filters.contains(MapConstants.MapPinKeys.FILTERS_WORKSPACE)){
                    double lat = (double) linkedTreeMap.get(MapConstants.MapPinKeys.LAT);
                    double lng = (double) linkedTreeMap.get(MapConstants.MapPinKeys.LNG);
                    if (withinUserRadius(lat, lng)){
                        workspacesWithinRadius.add(linkedTreeMap);
                        ArrayList<String> urls = (ArrayList) linkedTreeMap.get(MapConstants.MapPinKeys.IMGS);
                        String img_url = "";
                        if (urls.size() > 0){
                            img_url = urls.get(0);
                        }
                        img_urls.add(img_url);
                    }
                }
            }

        }

        @Override
        protected void onPostExecute(Void nulls){
            new ImageAsyncTask().execute(img_urls);
        }
    }

    private class ImageAsyncTask extends AsyncTask<ArrayList<String>, Void, Void> {

        @Override
        protected Void doInBackground(ArrayList<String>... urls){
            ArrayList<String> single_urls = urls[0];
            images = new ArrayList<>(single_urls.size());
            for (String url: single_urls){
                images.add(getImage(url));
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void nulls){
            initialized = true;
            if (onInitCompleteEventNotifier != null){
                onInitCompleteEventNotifier.onResponse(null);
            }
        }

        private Drawable getImage(String url) {
            if (!url.equals("")) {
                try {
                    Bitmap bitmap = Glide.with(PPSession.getContainerContext()).load(url)
                            .asBitmap().into(-1, -1).get();
                    return new BitmapDrawable(PPSession.getContainerContext().getResources(), bitmap);
                } catch (InterruptedException | ExecutionException e) {
                    Log.e("getImage", e.toString());
                }
            }
            return null;
        }

    }

}
