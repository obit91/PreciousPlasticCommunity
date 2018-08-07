package com.example.android.preciousplastic.activities;

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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.preciousplastic.fragments.FragmentAboutUs;
import com.example.android.preciousplastic.fragments.FragmentBazaar;
import com.example.android.preciousplastic.fragments.FragmentCart;
import com.example.android.preciousplastic.fragments.FragmentHome;
import com.example.android.preciousplastic.fragments.FragmentMyWorkshopNonOwner;
import com.example.android.preciousplastic.fragments.FragmentMyWorkshopOwner;
import com.example.android.preciousplastic.fragments.FragmentProfile;
import com.example.android.preciousplastic.fragments.FragmentSettings;
import com.example.android.preciousplastic.fragments.FragmentTest;
import com.example.android.preciousplastic.fragments.FragmentWorkshops;
import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.fragments.FragmentMap;
import com.example.android.preciousplastic.utils.PPSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HOME_ACTIVITY";

    private static final String PRECIOUS_PLASTIC_URL = "https://preciousplastic.com/";

    private FirebaseAuth mAuth = PPSession.getFirebaseAuth();
    private Class<? extends Fragment> currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        // updating container context
        PPSession.setContainerContext(this);

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

    /**
     * Signs out the current Firebase user.
     */
    private void signOut() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String mail = user.getEmail();
            mAuth.signOut();
            Log.i(TAG, mail + " Signed out.");
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
                //TODO: on register wait until current user is pulled (use callback functions).
                if (PPSession.getCurrentUser().isOwner()) {
                    goToFragment(FragmentMyWorkshopOwner.class);
                } else {
                    goToFragment(FragmentMyWorkshopNonOwner.class);
                }
                break;
            case R.id.drawer_settings:
                goToFragment(FragmentSettings.class);
                break;
            case R.id.drawer_my_cart:
                goToFragment(FragmentCart.class);
                break;
            case R.id.drawer_to_website:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PRECIOUS_PLASTIC_URL));
                startActivity(browserIntent);
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
     * Switches fragment according to input.
     *
     * @param fragmentClass type of fragment to use.
     */
    private void goToFragment(final Class<? extends Fragment> fragmentClass) {

        if (fragmentClass == currentFragment) {
            Toast.makeText(this, "Useless user is useless!", Toast.LENGTH_SHORT).show();
            return;
        }

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
            Log.i(TAG, "goToFragment: " + fragmentName);
        } catch (IllegalAccessException | InstantiationException e) {
            Log.e(TAG, "goToFragment: " + e.getMessage());
        }
    }

    /**
     * External method to switch fragments from fragments.
     * @param fragmentClass fragment to switch to.
     */
    public void switchFragment(final Class<? extends Fragment> fragmentClass) {
        goToFragment(fragmentClass);
    }
}

