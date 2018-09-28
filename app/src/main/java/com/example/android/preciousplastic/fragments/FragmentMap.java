package com.example.android.preciousplastic.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.activities.BaseActivity;
import com.example.android.preciousplastic.activities.MapActivity;
import com.example.android.preciousplastic.permissions.PermissionResponseHandler;
import com.example.android.preciousplastic.utils.PPSession;

import org.osmdroid.views.MapView;

public class FragmentMap extends BaseFragment implements PermissionResponseHandler {
    private MapActivity mapActivity = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    public void onStart() {
        super.onStart();

        setPermissionResponseHandler(this);

        mapActivity = PPSession.getMapActivity();
        mapActivity.setMapFragment(this);
        MapView mapView = getView().findViewById(R.id.map_view);
        mapActivity.buildMap(mapView);
    }

    @Override
    public boolean onBackPressed() {
        mapActivity.removeFilters();
        return false;
    }

    @Override
    public void permissionGranted(int permissionCode) {
        mapActivity.buildPartTwo();
    }

    @Override
    public void setPermissionResponseHandler(PermissionResponseHandler permissionResponseHandler) {
        final BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.setPermissionResponseHandler(this);
    }

    @Override
    public void permissionDenied(int permissionCode) {
        PPSession.getHomeActivity().onBackPressed();
    }
}
