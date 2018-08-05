package com.example.android.preciousplastic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.preciousplastic.Fragments.FragmentAboutUs;
import com.example.android.preciousplastic.Fragments.FragmentBazaar;
import com.example.android.preciousplastic.Fragments.FragmentCart;
import com.example.android.preciousplastic.Fragments.FragmentHome;
import com.example.android.preciousplastic.Fragments.FragmentMyWorkshop;
import com.example.android.preciousplastic.Fragments.FragmentProfile;
import com.example.android.preciousplastic.Fragments.FragmentSettings;
import com.example.android.preciousplastic.Fragments.FragmentTest;
import com.example.android.preciousplastic.Fragments.FragmentWorkshops;
import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.Fragments.FragmentMap;
import com.example.android.preciousplastic.db.PointsType;
import com.example.android.preciousplastic.db.entities.User;
import com.example.android.preciousplastic.db.repositories.UserRepository;
import com.example.android.preciousplastic.session.PPSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.osmdroid.views.MapView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private final String TAG = "HOME_ACTIVITY";

    private FirebaseAuth mAuth = PPSession.getFirebaseAuth();
    private UserRepository mUserRepository;

    private TextView userTextView = null;
    private MapActivity mapActivity = null;

    private TextView pointsTextView = null;
    private TextView pointsTypeTextView = null;

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        userTextView = (TextView) findViewById(R.id.home_text_mail);
        mapActivity = new MapActivity(this, this);
        mUserRepository = new UserRepository(this);

        // updating container context
        PPSession.setContainerContext(this);

        // setting listeners
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
        pointsTypeTextView = (TextView) findViewById(R.id.text_points_type);

        //Drawer Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String mail = currentUser.getEmail();
        userTextView.setText(mail);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Logs out the current user.
     *
     * @param view
     */
    public void onSignOutClick(View view) {
        Toast.makeText(this, "sign out", Toast.LENGTH_SHORT).show();
        signOut();
    }

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

    /**
     * Opens the map.
     *
     * @param view
     */
    public void onOpenMapClick(View view) {
        if (mapActivity != null) {
            setContentView(R.layout.activity_map);
            MapView mapView = (MapView) findViewById(R.id.activity_map);
            mapActivity.buildMap(mapView);
        }
    }

    public void onIncrementClick(View view) {
        User user = PPSession.currentUser();
        int pointTypeInt = Integer.parseInt(pointsTypeTextView.getText().toString());
        int pointsValueInt = Integer.parseInt(pointsTextView.getText().toString());
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
        User user = PPSession.currentUser();
        int pointTypeInt = Integer.parseInt(pointsTypeTextView.getText().toString());
        int pointsValueInt = Integer.parseInt(pointsTextView.getText().toString());
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
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.beta_testing:
                FragmentTest fragMap = FragmentTest.newInstance(null);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                        R.anim.enter_from_right, R.anim.exit_to_right);
                transaction.addToBackStack(null);
                transaction.add(R.id.fragmentContainer, fragMap, "FRAGMENT_TEST").commit();

                Toast.makeText(this, ("Clicked on " + "Testing"), Toast.LENGTH_SHORT).show();
                break;

            case R.id.drawer_home:

                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        new FragmentHome()).commit();
                Toast.makeText(this, ("Clicked on " + "Home"), Toast.LENGTH_SHORT).show();
                break;

            case R.id.drawer_workshops:

                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        new FragmentWorkshops()).commit();
                Toast.makeText(this, ("Clicked on " + "workshops"), Toast.LENGTH_SHORT).show();
                break;

            case R.id.drawer_bazaar:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        new FragmentBazaar()).commit();
                Toast.makeText(this, ("Clicked on " + "bazaar"), Toast.LENGTH_SHORT).show();
                break;

            case R.id.drawer_map:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        new FragmentMap()).commit();

                Toast.makeText(this, ("Clicked on " + "map"), Toast.LENGTH_SHORT).show();
                break;

            case R.id.drawer_profile:
                FragmentProfile fragProfile = FragmentProfile.newInstance(null, null);
                FragmentTransaction profTransaction = getSupportFragmentManager()
                        .beginTransaction();
                profTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                        R.anim.enter_from_right, R.anim.exit_to_right);
                profTransaction.addToBackStack(null);
                profTransaction.add(R.id.fragmentContainer, fragProfile, "FRAGMENT_PROFILE")
                        .commit();

                Toast.makeText(this, ("Clicked on " + "profile"), Toast.LENGTH_SHORT).show();
                break;

            case R.id.drawer_my_workshop:

                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        new FragmentMyWorkshop()).commit();
                Toast.makeText(this, ("Clicked on " + "My workshop"), Toast.LENGTH_SHORT).show();
                break;

            case R.id.drawer_settings:

                FragmentSettings fragmentSettings = FragmentSettings.newInstance(null, null);
                FragmentTransaction settTransaction = getSupportFragmentManager()
                        .beginTransaction();
                settTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                        R.anim.enter_from_right, R.anim.exit_to_right);
                settTransaction.addToBackStack(null);
                settTransaction.add(R.id.fragmentContainer, fragmentSettings, "FRAGMENT_SETTINGS")
                        .commit();
                Toast.makeText(this, ("Clicked on " + "settings"), Toast.LENGTH_SHORT).show();
                break;

            case R.id.drawer_my_cart:

                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        new FragmentCart()).commit();
                Toast.makeText(this, ("Clicked on " + "my cart"), Toast.LENGTH_SHORT).show();
                break;

            case R.id.drawer_to_website:

                // Todo link to open website using intent
                Toast.makeText(this, ("Clicked on " + "website"), Toast.LENGTH_SHORT).show();
                break;

            case R.id.drawer_about_us:

                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        new FragmentAboutUs()).commit();
                Toast.makeText(this, ("Clicked on " + "About us"), Toast.LENGTH_SHORT).show();
                break;

            case R.id.drawer_sign_out:
                signOut();
                Toast.makeText(this, ("Clicked on " + "Sign out"), Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

