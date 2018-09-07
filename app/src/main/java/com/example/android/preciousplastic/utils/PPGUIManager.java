package com.example.android.preciousplastic.utils;

import android.support.v4.app.Fragment;

import com.example.android.preciousplastic.fragments.FragmentProfile;

public class PPGUIManager {

    public static void updateGUI() {

        Fragment fragment = PPSession.getCurrentFragment();
        Class <? extends Fragment> fragmentClass = PPSession.getCurrentFragmentClass();

        if (fragmentClass == FragmentProfile.class) {
            final FragmentProfile fragmentProfile = (FragmentProfile) fragment;
            fragmentProfile.updateBins(null);
        }
    }

}
