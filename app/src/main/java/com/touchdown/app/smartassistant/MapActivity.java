package com.touchdown.app.smartassistant;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.touchdown.app.smartassistant.data.DbHelper;
import com.touchdown.app.smartassistant.models.LocationDao;
import com.touchdown.app.smartassistant.models.ReminderDao;
import com.touchdown.app.smartassistant.services.GeocoderTask;
import com.touchdown.app.smartassistant.services.MarkerManager;
import com.touchdown.app.smartassistant.views.DetailsActivity;

import java.util.List;

public class MapActivity extends ActionBarActivity implements GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerDragListener{

    private LocationManager locationManager;
    private MarkerManager markerManager;
    public static Context appCtx;

    // Google Map
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        appCtx = getApplicationContext();

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Button findLocationBtn = (Button) findViewById(R.id.findLocationBtn);
        findLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText locationInput = (EditText) findViewById(R.id.locationInput);
                String location = locationInput.getText().toString();
                if(location!=null && !location.equals("")){
                    new GeocoderTask(googleMap, getBaseContext(), MapActivity.this).execute(location);
                }
            }
        });
    }

    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initializeMap() {
        if (googleMap != null) {
            googleMap.clear();
        }
        else{
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(
                    R.id.map)).getMap();
            googleMap.setMyLocationEnabled(true);
            //    googleMap.getUiSettings().setZoomControlsEnabled(false);
            googleMap.getUiSettings().setRotateGesturesEnabled(false);
            googleMap.setOnMapLongClickListener(this);
            googleMap.setOnMapClickListener(this);
            googleMap.setOnMarkerClickListener(this);
            googleMap.setOnInfoWindowClickListener(this);
            googleMap.setOnMarkerDragListener(this);
            Location loc = getLocation();
            animateToLocation(loc);

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(this, R.string.loading_map_error, Toast.LENGTH_SHORT).show();
/*                Toast.makeText(getApplicationContext(),
                        R.string.loading_map_error, Toast.LENGTH_SHORT)
                        .show();*/
            }
        }
    }


    private Location getLocation(){
        int minTime = 0;
        List<String> matchingProviders = locationManager.getAllProviders();
        String prov = matchingProviders.get(0);
        Location loc = locationManager.getLastKnownLocation(prov);
        float bestAccuracy = loc.getAccuracy();
        Location bestResult = loc;
        long bestTime = loc.getTime();

        for (String provider: matchingProviders) {
            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                float accuracy = location.getAccuracy();
                long time = location.getTime();

                if ((time > minTime && accuracy < bestAccuracy)) {
                    bestResult = location;
                    bestAccuracy = accuracy;
                    bestTime = time;
                }
                else if (time < minTime &&
                        bestAccuracy == Float.MAX_VALUE && time > bestTime){
                    bestResult = location;
                    bestTime = time;
                }
            }
        }
        return bestResult;
    }

    private void animateToLocation(Location loc){
        CameraPosition pos = new CameraPosition.Builder().target(new LatLng(loc.getLatitude(), loc.getLongitude())).zoom(15).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos));
    }

    public void setMarker(Marker marker){
        markerManager.saveMarker(marker, null);
        markerManager.selectMarker(marker);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    //todo show the search field. If the search button is not pressed do not show it at all
    public void showSearch(){

    }

    public void startAddActivity(){
        if(!markerManager.userHasSelectedMarker()){
            Toast.makeText(this, "No location chosen!", Toast.LENGTH_SHORT);
        }else{
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra("location", markerManager.getSelectedMarker().getPosition());
            this.startActivity(intent);
        }
    }

    public void startEdit(ReminderDao r){
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("reminderID", r.getId());
        startActivity(intent);
    }

    private void confirmRemove(){
        if(markerManager.userHasSelectedMarker()){
            if(markerManager.removeSelectedIfEmpty()){
                supportInvalidateOptionsMenu();
            }else{
                showConfirmationPopUp();
            }
        }
    }

    private void showConfirmationPopUp(){
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to delete this reminder?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ReminderDao.remove(new DbHelper(MapActivity.appCtx), markerManager.getReminder(markerManager.getSelectedMarker()).getId());
                     //   removeProximityAlert();

                        markerManager.removeSelectedMarker();
                        supportInvalidateOptionsMenu();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void removeProximityAlert(){
        Intent proximityIntent = new Intent("com.artofcodeapps.locationalarm.app.Views.MenuActivity");
      //  PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, proximityIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
    //    locationManager.removeProximityAlert(pendingIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapLongClick(LatLng point) {
        Marker marker = markerManager.generateMarker("No reminder", point, MarkerManager.getSelectedColor());
        markerManager.saveMarker(marker, null);
        markerManager.selectMarker(marker);
        startAddActivity();
        //supportInvalidateOptionsMenu();
    }

    @Override
    protected void onResume() {
        initializeMap();
        markerManager = new MarkerManager(googleMap, this);
        super.onResume();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        markerManager.selectMarker(marker);
        supportInvalidateOptionsMenu();
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        markerManager.unSelectMarker();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        ReminderDao reminder = markerManager.getReminder(marker);
        if(reminder != null){
            startEdit(reminder);
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        markerManager.removeRadiusFromMap();
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        ReminderDao reminder = markerManager.getReminder(marker);
        if(reminder != null){
            LocationDao loc = reminder.getLocation();
            loc.setLocation(marker.getPosition());
            loc.update(new DbHelper(this));
            markerManager.selectMarker(marker);
        }
    }


}
