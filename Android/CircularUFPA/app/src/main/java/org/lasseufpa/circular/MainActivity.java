package org.lasseufpa.circular;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.lasseufpa.circular.data.StopList;
import org.lasseufpa.circular.helpers.MapHelper;
import org.lasseufpa.circular.helpers.MqttHelper;
import org.lasseufpa.circular.utils.UfpaStopList;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    MapView map;

    private final float ZOOM_LEVEL = 17;
    private final double INICIAL_LAT = -1.473590;
    private final double INICIAL_LONG = -48.451329;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Setup the application
        setupMap();
        setupMqtt();
    }

    public void setupMap(){
        // load/initialize the osmdroid configuration
        Context context = getApplicationContext();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        //User-Agent variable. Identifies the user to OSM servers.
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        // Zoom configuration
        map.setMultiTouchControls(true);
        map.getController().setZoom(ZOOM_LEVEL);

        GeoPoint startPoint = new GeoPoint(INICIAL_LAT, INICIAL_LONG);
        map.getController().setCenter(startPoint);

        addStopMarkers();
    }

    public void addStopMarkers(){
        Drawable stopsMarker = this.getResources().getDrawable(R.drawable.stop_point);
        MapHelper mapHelper = new MapHelper();
        ArrayList<OverlayItem> items = mapHelper.addStopOverlay(UfpaStopList.STOPS, stopsMarker);

        ItemizedIconOverlay stopOverlay = new ItemizedIconOverlay(this, items, new ItemizedIconOverlay.OnItemGestureListener() {
            @Override
            public boolean onItemSingleTapUp(int index, Object item) {
                return false;
            }

            @Override
            public boolean onItemLongPress(int index, Object item) {
                return false;
            }
        });

        map.getOverlays().add(stopOverlay);
    }

    public void setupMqtt(){
        MqttHelper mqtt = new MqttHelper(getApplicationContext());
        mqtt.connect();

        if (mqtt.isConnected()){
            Log.w("MQTT", "Connected");
        } else {
            Log.w("MQTT", "Not connected");
        }
    }

    public void onResume(){
        super.onResume();
        map.onResume(); // This will refresh the osmdroid configuration on resuming

    }

    public void onPause(){
        super.onPause();
        map.onPause();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {
            // Handle the camera action
        } else if (id == R.id.nav_active_fleet) {

        } else if (id == R.id.nav_stops) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_feedback) {

        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
