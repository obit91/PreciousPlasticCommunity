package com.community.android.preciousplastic.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.community.android.preciousplastic.fragments.BaseFragment;
import com.community.android.preciousplastic.fragments.FragmentPerformRecycle;
import com.community.android.preciousplastic.fragments.FragmentPerformTrade;
import com.community.android.preciousplastic.fragments.FragmentTutorial;
import com.community.android.preciousplastic.imgur.ImgurConstants;
import com.community.android.preciousplastic.utils.Transitions;
import com.community.android.preciousplastic.utils.Transitions.TransitionTypes;

import com.community.android.preciousplastic.R;
import com.community.android.preciousplastic.fragments.FragmentAboutUs;
import com.community.android.preciousplastic.fragments.FragmentHome;
import com.community.android.preciousplastic.fragments.FragmentMap;
import com.community.android.preciousplastic.fragments.FragmentMyWorkspaceNonOwner;
import com.community.android.preciousplastic.fragments.FragmentMyWorkspaceOwner;
import com.community.android.preciousplastic.fragments.FragmentProfile;
import com.community.android.preciousplastic.fragments.FragmentSettings;
import com.community.android.preciousplastic.fragments.FragmentWorkspaces;
import com.community.android.preciousplastic.adaptors.WorkspaceAdaptor;
import com.community.android.preciousplastic.fragments.FragmentBazaar;
import com.community.android.preciousplastic.utils.PPSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HOME_ACTIVITY";
    private boolean showTutorial = false;
    private static final String PRECIOUS_PLASTIC_URL = "https://preciousplastic.com/";

    private FirebaseAuth mAuth = PPSession.getFirebaseAuth();
    private NavigationView navigationView = null;

    TransitionTypes transitionType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);


        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            final Serializable potentialTransitionType = extras.getSerializable(Transitions.TRANSITION_TYPE);
            if (potentialTransitionType != null) {
                transitionType = (TransitionTypes) extras.getSerializable(Transitions.TRANSITION_TYPE);
                showTutorial = true;
            }
        }

        // updating container context
        PPSession.setContainerContext(this);

        // setting Retrofit.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ImgurConstants.IMGUR_URL_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PPSession.setRetrofit(retrofit);

        //Drawer Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateOwnerButtons();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        PPSession.setMapActivity(new MapActivity());
        PPSession.setWorkspacesActivity(new WorkspacesActivity());

        LayoutInflater inflater = (LayoutInflater) PPSession.getContainerContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        PPSession.setWorkspaceAdaptor(new WorkspaceAdaptor(inflater));
    }

    /**
     * Turns recycle icon visible for owners.
     */
    public void updateOwnerButtons() {
        // Set owner buttons unclickable for non-owners.
        final Menu menu = navigationView.getMenu();
        final MenuItem recycleIcon = menu.findItem(R.id.drawer_recycle);
        recycleIcon.setVisible(PPSession.getCurrentUser().isOwner());
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("activeFragment", PPSession.getCurrentFragmentClass());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        final Class<? extends Fragment> currentFragmentClass = PPSession.getCurrentFragmentClass();
        showTutorial = false;

        /*
         * Restoring the previous opened fragment.
         */
        if (currentFragmentClass != null && currentFragmentClass != FragmentHome.class
                && savedInstanceState != null) {
            Serializable activeFragment = savedInstanceState.getSerializable("activeFragment");
            BaseFragment fragment = (BaseFragment) activeFragment;
            goToFragment(fragment.getClass());
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        showTutorial = false;
    }

    @Override
    public void onStart() {
        super.onStart();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (PPSession.getCurrentFragmentClass() == null) {
            goToFragment(FragmentHome.class);
        }

        if (transitionType != null && transitionType == TransitionTypes.REGISTER && showTutorial) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name);
            builder.setMessage("Welcome to Precious Plastic Community!\nIt seems that we haven't met before,\nwould you like to go through a short tutorial?");
//            builder.setIcon(R.drawable.ic_launcher);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    goToFragment(FragmentTutorial.class);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    /**
     * Handles a specific fragment's back press.
     */
    private boolean checkFragmentBackPressed() {
        final BaseFragment currentFragment = PPSession.getCurrentFragment();
        return currentFragment.onBackPressed();
    }

    @Override
    public void onBackPressed() {

        boolean backPressHandled = checkFragmentBackPressed();
        if (backPressHandled) {
            return;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (PPSession.getCurrentFragmentClass() == FragmentHome.class) {
//                Toast.makeText(this, "lol no turning back", Toast.LENGTH_SHORT).show();
                // minimizes app
                moveTaskToBack(true);
            } else {
                goToFragment(FragmentHome.class);
            }
        }
    }

    /**
     * Signs out the current Firebase user.
     */
    private void signOut() {
//        TransitionTypes type = TransitionTypes.SIGN_OUT;
//        Intent i = new Intent(this, LoadingActivity.class);
//        i.putExtra(Transitions.TRANSITION_TYPE, type);
//        startActivity(i);
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            PPSession.initSession(TransitionTypes.SIGN_OUT);
        }
        finish();
    }

    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // first close the drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawers();

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
//            case R.id.beta_testing:
//                goToFragment(FragmentTest.class);
//                break;
            case R.id.drawer_home:
                goToFragment(FragmentHome.class);
                break;
            case R.id.trade_frag:
                goToFragment(FragmentPerformTrade.class);
                break;
            case R.id.drawer_workspaces:
                goToFragment(FragmentWorkspaces.class);
                break;
            /*case R.id.drawer_edit_my_workspace:
                goToFragment(FragmentEditMyWorkspace.class);
                break;*/
            case R.id.drawer_recycle:
                goToFragment(FragmentPerformRecycle.class);
                break;
            case R.id.drawer_bazaar:
                goToFragment(FragmentBazaar.class);
                break;
            case R.id.drawer_map:
                goToFragment(FragmentMap.class);
                break;
            case R.id.drawer_profile:
                goToFragment(FragmentProfile.class);
                break;
            case R.id.drawer_my_workspace:
                //TODO: on register wait until current user is pulled (use callback functions).
                if (PPSession.getCurrentUser().isOwner()) {
                    goToFragment(FragmentMyWorkspaceOwner.class);
                } else {
                    goToFragment(FragmentMyWorkspaceNonOwner.class);
                }
                break;
            case R.id.drawer_settings:
                goToFragment(FragmentSettings.class);
                break;
            case R.id.drawer_to_website:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PRECIOUS_PLASTIC_URL));
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(browserIntent);
                break;
            case R.id.drawer_about_us:
                goToFragment(FragmentAboutUs.class);
                break;
            case R.id.drawer_tutorial:
                goToFragment(FragmentTutorial.class);
                break;
            case R.id.drawer_sign_out:
                signOut();
                finish();
                break;
            default:
                break;
        }

        return true;
    }

    /**
     * Switches fragment according to input.
     *
     * @param fragmentClass type of fragment to use.
     */
    private void goToFragment(final Class<? extends BaseFragment> fragmentClass) {

        if (fragmentClass == PPSession.getCurrentFragmentClass()) {
//            String msg = "Useless user is useless! <%s>";
//            Toast.makeText(this, String.format(msg, fragmentClass.getSimpleName()), Toast.LENGTH_SHORT).show();
            return;
        }

        performFragmentSwitch(fragmentClass);
    }

    /**
     * Implements a fragment switch.
     *
     * @param fragmentClass fragment to switch to.
     */
    private void performFragmentSwitch(Class<? extends BaseFragment> fragmentClass) {
        try {
            // retrieve details of given fragment class.
            BaseFragment fragment = fragmentClass.newInstance();
            String fragmentName = fragmentClass.getSimpleName();

            // update current fragment.
            PPSession.setCurrentFragment(fragment);

            // transfer control to the new fragment.
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                    R.anim.enter_from_right, R.anim.exit_to_right);
            transaction.addToBackStack(null);
            transaction.replace(R.id.fragmentContainer, fragment, fragmentName);
            transaction.commit();
            Log.i(TAG, "goToFragment: " + fragmentName);
        } catch (IllegalAccessException | InstantiationException e) {
            Log.e(TAG, "goToFragment: " + e.getMessage());
        }
    }

    /**
     * External method to switch fragments from fragments.
     *
     * @param fragmentClass fragment to switch to.
     */
    public void switchFragment(final Class<? extends BaseFragment> fragmentClass) {
        goToFragment(fragmentClass);
    }
}

