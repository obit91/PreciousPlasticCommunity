package com.community.android.preciousplastic.fragments;

import android.support.v4.app.Fragment;
import android.view.View;

import com.community.android.preciousplastic.utils.OnBackPressed;

public abstract class BaseFragment extends Fragment implements OnBackPressed {

    /**
     * Can be used by fragments to update GUI. This class is called when user data is updated.
     */
    public void updateGUI(View view) {}
}
