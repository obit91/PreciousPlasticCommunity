package com.example.android.preciousplastic.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.preciousplastic.fragments.FragmentAboutUs;
import com.example.android.preciousplastic.fragments.FragmentBazaar;
import com.example.android.preciousplastic.fragments.FragmentCart;
import com.example.android.preciousplastic.fragments.FragmentHome;
import com.example.android.preciousplastic.fragments.FragmentMyWorkshop;
import com.example.android.preciousplastic.fragments.FragmentProfile;
import com.example.android.preciousplastic.fragments.FragmentSettings;
import com.example.android.preciousplastic.fragments.FragmentTest;
import com.example.android.preciousplastic.fragments.FragmentWorkshops;
import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.fragments.FragmentMap;
import com.example.android.preciousplastic.db.PointsType;
import com.example.android.preciousplastic.db.entities.User;
import com.example.android.preciousplastic.db.repositories.UserRepository;
import com.example.android.preciousplastic.session.PPSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.osmdroid.views.MapView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "HOME_ACTIVITY";

    private FirebaseAuth mAuth = PPSession.getFirebaseAuth();
    /*
    private UserRepository mUserRepository;
    private MapActivity mapActivity = null;
*/
    private Class<? extends Fragment> currentFragment;
/*
    private TextView pointsTextView = null;
    private TextView pointsTypeTextView = null*/;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
/*        mapActivity = new MapActivity(this, this);
        mUserRepository = new UserRepository(this);*/

        // updating container context
        PPSession.setContainerContext(this);

/*        // setting listeners
        Button signOutButton = (Button) findViewById(R.id.button_sign_out);
        Button mapButton = (Button) findViewById(R.id.button_map);
        Button incrementButton = (Button) findViewById(R.id.button_increment);
        Button decrementButton = (Button) findViewById(R.id.button_decrement);
        signOutButton.setOnClickListener(this);
        mapButton.setOnClickListener(this);
        incrementButton.setOnClickListener(this);
        decrementButton.setOnClickListener(this);

        // points values
        pointsTextView = (TextView) findViewById(R.id.text_points_value);
        pointsTypeTextView = (TextView) findViewById(R.id.text_points_type);*/

        //Drawer Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onStart() {
        super.onStart();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        goToFragment(FragmentHome.class);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (currentFragment == FragmentHome.class) {
                Toast.makeText(this, "lol no turning back", Toast.LENGTH_SHORT).show();
            } else {
                goToFragment(FragmentHome.class);
//                super.onBackPressed();
            }
        }
    }

    /*    *//**
     * Logs out the current user.
     *
     * @param view
     *//*
    public void onSignOutClick(View view) {
        Toast.makeText(this, "sign out", Toast.LENGTH_SHORT).show();
        signOut();
    }*/

    /**
     * Signs out the current Firebase user.
     */
    private void signOut() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String mail = user.getEmail();
            mAuth.signOut();
            Log.d(TAG, mail + " Signed out.");
        }
        returnToSignIn();
    }

    /**
     * Switches activity to the sign-in intent (usually after sign out).
     */
    private void returnToSignIn() {
        Intent signInIntent = new Intent(this, WelcomeActivity.class);
        startActivity(signInIntent);
    }

    /*    *//**
     * Opens the map.
     *
     * @param view
     *//*
    public void onOpenMapClick(View view) {
        if (mapActivity != null) {
            setContentView(R.layout.activity_map);
            MapView mapView = (MapView) findViewById(R.id.activity_map);
            mapActivity.buildMap(mapView);
        }
    }

    public void onIncrementClick(View view) {
        User user = PPSession.getCurrentUser();
        int pointTypeInt = Integer.parseInt(pointsTypeTextView.getText().toString());
        long pointsValueInt = Long.parseLong(pointsTextView.getText().toString());
        PointsType type = PointsType.getType(pointTypeInt);
        user.addPoints(type, pointsValueInt);
        mUserRepository.updateUser(user);
    }

    public void onOpenDrawer(View view) {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//

    }

    public void onDecrementClick(View view) {
        User user = PPSession.getCurrentUser();
        int pointTypeInt = Integer.parseInt(pointsTypeTextView.getText().toString());
        long pointsValueInt = Long.parseLong(pointsTextView.getText().toString());
        user.removePoints(PointsType.getType(pointTypeInt), pointsValueInt);
        mUserRepository.updateUser(user);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.button_sign_out):
                onSignOutClick(view);
                break;
            case (R.id.button_map):
                onOpenMapClick(view);
                break;
            case (R.id.button_increment):
                onIncrementClick(view);
                break;
            case (R.id.button_decrement):
                onDecrementClick(view);
                break;
        }
    }*/

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
            case R.id.beta_testing:
                goToFragment(FragmentTest.class);
                break;
            case R.id.drawer_home:
                goToFragment(FragmentHome.class);
                break;
            case R.id.drawer_workshops:
                goToFragment(FragmentWorkshops.class);
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
            case R.id.drawer_my_workshop:
                goToFragment(FragmentMyWorkshop.class);
                break;
            case R.id.drawer_settings:
                goToFragment(FragmentSettings.class);
                break;
            case R.id.drawer_my_cart:
                goToFragment(FragmentCart.class);
                break;
            case R.id.drawer_to_website:
                // Todo link to open website using intent
                Toast.makeText(this, ("Clicked on " + "website"), Toast.LENGTH_SHORT).show();
                break;
            case R.id.drawer_about_us:
                goToFragment(FragmentAboutUs.class);
                break;
            case R.id.drawer_sign_out:
                signOut();
                break;
            default:
                break;
        }

        return true;
    }

    /**
     * 3
     * Switches fragment according to input.
     *
     * @param fragmentClass type of fragment to use.
     */
    private void goToFragment(final Class<? extends Fragment> fragmentClass) {
        try {
            // update current fragment.
            currentFragment = fragmentClass;

            // retrieve details of given fragment class.
            Fragment fragment = fragmentClass.newInstance();
            String fragmentName = fragmentClass.getSimpleName();

            // transfer control to the new fragment.
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                    R.anim.enter_from_right, R.anim.exit_to_right);
            transaction.addToBackStack(null);
            transaction.replace(R.id.fragmentContainer, fragment, fragmentName);
            transaction.commit();
            Toast.makeText(this, ("Clicked on " + fragmentName), Toast.LENGTH_SHORT).show();
        } catch (IllegalAccessException | InstantiationException e) {
            System.err.println("Invalid class");
            e.printStackTrace();
            Log.e(TAG, "goToFragment: class exception");
        }
    }
}

