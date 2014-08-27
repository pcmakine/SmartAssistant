package com.touchdown.app.smartassistant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.touchdown.app.smartassistant.newdb.Task;
import com.touchdown.app.smartassistant.newdb.TaskManager;
import com.touchdown.app.smartassistant.newdb.TriggerLocation;
import com.touchdown.app.smartassistant.services.MyLocationProvider;
import com.touchdown.app.smartassistant.services.GeocoderTask;
import com.touchdown.app.smartassistant.services.Markers.MarkerManager;
import com.touchdown.app.smartassistant.views.DetailsActivity;
import com.touchdown.app.smartassistant.views.RemoveTasksListener;
import com.touchdown.app.smartassistant.views.RemoveTasksTask;
import com.touchdown.app.smartassistant.views.UpdateTaskListener;
import com.touchdown.app.smartassistant.views.UpdateTaskTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MapActivity extends ActionBarActivity implements GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerDragListener, Observer, RemoveTasksListener, UpdateTaskListener{

    public static final String LOG_TAG = MapActivity.class.getSimpleName();

    private MarkerManager markerManager;
    private TaskManager taskManager;
    private boolean onCreateRan;
    private MyLocationProvider locProvider;
    private EditText addressField;

    // Google Map
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        taskManager = TaskManager.getInstance(this);

        locProvider = new MyLocationProvider(this);
        final Button findLocationBtn = (Button) findViewById(R.id.findLocationBtn);
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
        initializeMap();
        markerManager = new MarkerManager(googleMap);
        onCreateRan = true;

        addressField = (EditText) findViewById(R.id.locationInput);
        addressField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d(LOG_TAG, "keycode: " + keyCode);
                if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER){
                    findLocationBtn.performClick();
                    return false;
                }
                return false;
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
            android.location.Location loc = locProvider.getLocation();
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

    private void animateToLocation(android.location.Location loc){
        CameraPosition pos = new CameraPosition.Builder().target(new LatLng(loc.getLatitude(), loc.getLongitude())).zoom(15).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos));
    }

    public void setMarker(Marker marker){
        markerManager.saveMarker(marker, null);
        markerManager.selectMarker(marker);
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

    public void startEdit(Task r){
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("reminderID", r.getId());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_activity_actions, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteEmptyOrConfirmRemove();
                return true;
            case R.id.action_view_as_list:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteEmptyOrConfirmRemove(){
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
                .setMessage(R.string.remove_confirmation)
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        long id = markerManager.getTask(markerManager.getSelectedMarker()).getId();
                        markerManager.removeSelectedMarker();

                       // taskManager.removeTask(id);
                        removeTask(id);
                        supportInvalidateOptionsMenu();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void removeTask(long id){
        List idList = new ArrayList();
        idList.add(id);
        new RemoveTasksTask(this).execute(idList);
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
        taskManager.addObserver(this);
        if(!onCreateRan){
            markerManager.updateMarkerData();
            markerManager.unSelectMarker();
        }
        super.onResume();
    }

    @Override
    protected void onPause(){
        onCreateRan = false;
        taskManager.deleteObserver(this);
        super.onPause();
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
/*        Marker mark = markerManager.getMarkerFromRadiusClick(latLng);
        if(mark != null){
            onMarkerDragStart(mark);
        }*/
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Task task = markerManager.getTask(marker);
        if(task != null){
            startEdit(task);
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        markerManager.hideRadius(marker);
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public synchronized void onMarkerDragEnd(Marker marker) {
        Task task = markerManager.getTask(marker);
        if(task != null){
            TriggerLocation loc = (TriggerLocation) task.getTrigger();
            loc.setLatLng(marker.getPosition());
            markerManager.selectMarker(marker);
            markerManager.updateRadiusLocation(marker);
            //markerManager.showRadius(marker);
            new UpdateTaskTask(this, false, false).execute(task);   //todo how to save the selected marker... the update will overwrite it
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                markerManager.updateMarkerData();
            }
        });
    }

    @Override
    public void removeSuccessful(boolean success) {
        if(success){
            Toast.makeText(this, R.string.remove_success, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, R.string.remove_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void updateSuccessful(boolean success) {
        if(success){
            Toast.makeText(this, R.string.update_success, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, R.string.update_error, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public LinearLayout getOnProgressIndicator() {
        return null;
    }
}
