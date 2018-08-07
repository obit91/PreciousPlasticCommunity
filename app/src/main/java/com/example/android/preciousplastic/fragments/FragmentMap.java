package com.example.android.preciousplastic.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.activities.MapActivity;
import com.example.android.preciousplastic.utils.PPSession;

import org.osmdroid.views.MapView;

public class FragmentMap extends Fragment {
    private MapActivity mapActivity = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    public void onStart() {
        super.onStart();

        mapActivity = new MapActivity(PPSession.getContainerContext(), PPSession.getHomeActivity());
        MapView mapView = (MapView) getView().findViewById(R.id.fragment_map);
        mapActivity.buildMap(mapView);
    }
}
