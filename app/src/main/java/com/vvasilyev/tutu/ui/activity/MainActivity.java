package com.vvasilyev.tutu.ui.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.vvasilyev.tutu.R;
import com.vvasilyev.tutu.model.SimpleStation;

/**
 *
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.screen_title_schedule);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        Fragment fragment = new FormFragment();
        getFragmentManager().beginTransaction().replace(R.id.content, fragment, FormFragment.TAG).commit();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            SimpleStation station = (SimpleStation) data.getSerializableExtra(StationPickerActivity.STATION_EXTRA);
            Fragment fragment = getFragmentManager().findFragmentByTag(FormFragment.TAG);
            if (fragment != null && fragment instanceof FormFragment) {
                FormFragment form = (FormFragment) fragment;
                form.selected(station, requestCode);
            }
        }
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.schedule) {
            Fragment fragment = new FormFragment();
            getFragmentManager().beginTransaction().replace(R.id.content, fragment, FormFragment.TAG).commit();
            toolbar.setTitle(R.string.screen_title_schedule);
        } else if (id == R.id.about) {
            Fragment fragment = new AboutFragment();
            getFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
            toolbar.setTitle(R.string.screen_title_about);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
