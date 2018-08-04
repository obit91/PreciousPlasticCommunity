package com.example.android.preciousplastic.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.preciousplastic.R;

public class NavigationDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id)
        {

            case R.id.drawer_home:
            {
                Toast.makeText(this, ("Clicked on " + "Home"), Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.drawer_workshops:
            {
                Toast.makeText(this, ("Clicked on " + "workshops"), Toast.LENGTH_SHORT).show();

                break;
            }
            case R.id.drawer_bazaar:
            {
                Toast.makeText(this, ("Clicked on " + "bazaar"), Toast.LENGTH_SHORT).show();

                break;
            }
            case R.id.drawer_map:
            {
                Toast.makeText(this, ("Clicked on " + "map"), Toast.LENGTH_SHORT).show();

                break;
            }
            case R.id.drawer_profile:
            {
                Toast.makeText(this, ("Clicked on " + "profile"), Toast.LENGTH_SHORT).show();

                break;
            }
            case R.id.drawer_my_workshop:
            {
                Toast.makeText(this, ("Clicked on " + "My workshop"), Toast.LENGTH_SHORT).show();

                break;
            }
            case R.id.drawer_settings:
            {
                Toast.makeText(this, ("Clicked on " + "settings"), Toast.LENGTH_SHORT).show();

                break;
            }
            case R.id.drawer_my_cart:
            {
                Toast.makeText(this, ("Clicked on " + "my cart"), Toast.LENGTH_SHORT).show();

                break;
            }
            case R.id.drawer_to_website:
            {
                Toast.makeText(this, ("Clicked on " + "website"), Toast.LENGTH_SHORT).show();

                break;
            }
            case R.id.drawer_about_us:
            {
                Toast.makeText(this, ("Clicked on " + "About us"), Toast.LENGTH_SHORT).show();

                break;
            }
            case R.id.drawer_sign_out:
            {
                Toast.makeText(this, ("Clicked on " + "Sign out"), Toast.LENGTH_SHORT).show();

                break;
            }
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
