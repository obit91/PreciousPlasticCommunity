package com.example.android.preciousplastic.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.activities.MapConstants;
import com.example.android.preciousplastic.db.EventNotifier;
import com.example.android.preciousplastic.utils.InternetQuery;
import com.example.android.preciousplastic.utils.PPSession;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.zip.Inflater;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentWorkspaces.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentWorkspaces#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentWorkspaces extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private int radius = 4000;

    private RecyclerView mRecyclerView;
    private WorkspaceAdaptor mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private LayoutInflater mInflater;

    boolean haveSetData = false;
    boolean haveSetImgs = false;

    public FragmentWorkspaces()
    { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentWorkspaces.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentWorkspaces newInstance(String param1, String param2)
    {
        FragmentWorkspaces fragment = new FragmentWorkspaces();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        mInflater = inflater;
        View v = inflater.inflate(R.layout.fragment_workspaces, container, false);

        // get workspaces
        InternetQuery.queryPins(new WorkspacesQueryEventNotifier());

        // setup RecyclerView and Adaptor
        mRecyclerView = v.findViewById(R.id.workspaces_recycleview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(PPSession.getContainerContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        }
        else
        {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Extract all workspaces and set them in view.
     * @param pinsList list of all pins on map (workspaces need to be filtered)
     */
    private void initWorkspaces(ArrayList pinsList){
        ArrayList<LinkedTreeMap<String, Object>> workspacesWithinRadius = new ArrayList<>();
        ArrayList<String> img_urls = new ArrayList<>();
        Log.i("initWorkspaces", String.valueOf(pinsList.size()));

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
        new ImageAsyncTask().execute(img_urls);
        setWorkspacesOnView(workspacesWithinRadius);
    }

    /**
     * Setup workspaces on view
     * @param pinsList list of workspaces which are within correct radius
     */
    private void setWorkspacesOnView(ArrayList pinsList){
        mAdapter = new WorkspaceAdaptor(mInflater);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setData(pinsList);
        haveSetData = true;
        if (haveSetImgs){
            mAdapter.notifyDataSetChanged();
        }
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
                ArrayList pinsArrayList = (ArrayList) response;
                initWorkspaces(pinsArrayList);
            } catch (ClassCastException e){
                onError(e.toString());
            }
        }
    }

    private class ImageAsyncTask extends AsyncTask<ArrayList<String>, Void, Void> {

        ArrayList<Drawable> imgs;

        @Override
        protected Void doInBackground(ArrayList<String>... urls){
            ArrayList<String> single_urls = urls[0];
            imgs = new ArrayList<>(single_urls.size());
            for (String url: single_urls){
                imgs.add(getImage(url));
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void nulls){
            Log.i("AsyncTask", "onPostExecute");
            mAdapter.setImagesData(imgs);
            haveSetImgs = true;
            if (haveSetData){
                mAdapter.notifyDataSetChanged();
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
