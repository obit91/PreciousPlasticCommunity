package com.community.android.preciousplastic.utils;

import android.support.v4.app.Fragment;

import com.community.android.preciousplastic.fragments.BaseFragment;
import com.community.android.preciousplastic.fragments.FragmentProfile;

public class PPGUIManager {

    public static void updateGUI() {
        BaseFragment currentFragment = PPSession.getCurrentFragment();
        if (currentFragment != null) {
            currentFragment.updateGUI(null);
        }
    }

}
