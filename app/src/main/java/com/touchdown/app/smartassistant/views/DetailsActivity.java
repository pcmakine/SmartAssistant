package com.touchdown.app.smartassistant.views;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.touchdown.app.smartassistant.R;
import com.touchdown.app.smartassistant.data.DbHelper;
import com.touchdown.app.smartassistant.models.LocationDao;
import com.touchdown.app.smartassistant.models.ReminderDao;
import com.touchdown.app.smartassistant.services.ProximityIntentReceiver;


//todo https://github.com/BoD/android-switch-backport if this is in use include apache v2.0 license somehow
public class DetailsActivity extends ActionBarActivity {
    public static final String LOG_TAG = DetailsActivity.class.getSimpleName();

    private static final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATE = 10000; // in Milliseconds
    private static final long PROX_ALERT_EXPIRATION = 1000*60*60*24*2; //in milliseconds two days
    private static final String PROX_ALERT_INTENT = "com.touchdown.smartassistant.app.Views.MapActivity";

    private static final int MIN_RADIUS_METERS = 20;
    private static final int MAX_RADIUS_METERS = 5000;
    private static final int SEEKBAR_MULTIPLIER_BELOW_KILOMETER = 10;
    private static final int SEEKBAR_MULTIPLIER_OVER_KILOMETER = 100;
    private static final int SEEKBAR_MULTIPLIER_CHANGE_TRESHOLD = 1000;

    private SQLiteOpenHelper dbHelper;
    private LatLng location;
    private String reminderText;
    private SeekBar radiusBar;
    private int radius;

    private TextView contentToSaveTW;
    private TextView radiusTW;
    private CompoundButton onSwitch;
    private TextView locationText;
    private ReminderDao reminder;
    private boolean editMode;
    private LocationManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        dbHelper = new DbHelper(this);
        manager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        locationText = (TextView) findViewById(R.id.location);
        contentToSaveTW = (TextView) findViewById(R.id.contentToSave);
        radiusTW = (TextView) findViewById(R.id.radius);

        setUpCompoundButton();

        Intent intent = getIntent();
        if(noReminderIdInExtras(intent)){
            makeNewReminder();
        }else{
            long id = intent.getLongExtra("reminderID", -1);
            ReminderDao reminder = ReminderDao.getOne(dbHelper, id);
            useExistingReminder(reminder);
        }

        if(location != null){
            locationText.setText(location.toString());
        }
        setUpSeekBar();
    }

    private boolean noReminderIdInExtras(Intent intent){
        return intent.getLongExtra("reminderID", -1) == -1;
    }

    private void makeNewReminder(){
        this.location = getIntent().getParcelableExtra("location");
        if(location != null){
            this.reminder = new ReminderDao(-1, null, new LocationDao(-1, -1, location, radius));
        }else{
            this.reminder = new ReminderDao(-1, null, null);
        }
        onSwitch.setChecked(true);
        editMode = false;
    }

    private void useExistingReminder(ReminderDao reminder){
        this.reminder = reminder;
        this.location = reminder.getLocation().getLatLng();
        contentToSaveTW.setText(reminder.getContent());
        onSwitch.setChecked(reminder.isOn());
        editMode = true;
    }


    private void setUpSeekBar(){
        radiusBar = (SeekBar)findViewById(R.id.seekBar);
        radiusBar.setProgress(MIN_RADIUS_METERS * 10);

        radiusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int progressMultiplierTreshold = SEEKBAR_MULTIPLIER_CHANGE_TRESHOLD/SEEKBAR_MULTIPLIER_BELOW_KILOMETER - MIN_RADIUS_METERS;

                if(progress < progressMultiplierTreshold){
                    radius = progress * SEEKBAR_MULTIPLIER_BELOW_KILOMETER + MIN_RADIUS_METERS;
                }else{
                    radius = SEEKBAR_MULTIPLIER_CHANGE_TRESHOLD + (progress - progressMultiplierTreshold) * SEEKBAR_MULTIPLIER_OVER_KILOMETER;
                }
                radiusTW.setText(getRadiusText());
            }

            private String getRadiusText(){
                if(radius < 1000){
                    return getResources().getString(R.string.radius) + " " + radius + " "  +
                            getResources().getString(R.string.meters);
                }else{
                    return  getResources().getString(R.string.radius) + " " + 1.0*radius/1000*1.0 + " " +
                            getResources().getString(R.string.kilometers);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        radiusBar.setMax((MAX_RADIUS_METERS - MIN_RADIUS_METERS)/10);
    }

    private void setUpCompoundButton(){
        onSwitch = (CompoundButton) findViewById(R.id.myswitch);
        onSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    reminder.turnOn();
                    //addProximityAlert();
                }else{
                    reminder.turnOff();
                    //removeProximityAlert();
                }
            }
        });
    }

    public void add(View view){

        reminderText  = contentToSaveTW.getText().toString();
        reminder.setContent(reminderText);

        if(editMode){
            updateReminder();
        }else{
            addReminder();
        }
    }

    private void updateReminder(){
        if(reminder.update(dbHelper)){
            if(onSwitch.isChecked()){
               // addProximityAlert();
            }else{
               // removeProximityAlert();
            }
            Toast toast = Toast.makeText(this, R.string.successfully_edited, Toast.LENGTH_LONG);
            toast.show();
            onBackPressed();
        }else{
            Toast toast = Toast.makeText(this, R.string.reminder_not_edited, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void addReminder(){
        if(reminder.insert(dbHelper) != -1){
            //addProximityAlert();
            Toast toast = Toast.makeText(this, R.string.successfully_added, Toast.LENGTH_LONG);
            toast.show();
            onBackPressed();
        }else{
            Toast toast = Toast.makeText(this, R.string.reminder_not_added, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void addProximityAlert(){
        if(reminder.getLocation() != null){
            Intent intent = new Intent(PROX_ALERT_INTENT);
            intent.putExtra("reminderID", reminder.getId());
            PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            manager.addProximityAlert(reminder.getLocation().getLatLng().latitude,
                    reminder.getLocation().getLatLng().longitude,
                    reminder.getLocation().getRadius(),
                    PROX_ALERT_EXPIRATION,
                    proximityIntent);
            IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
            registerReceiver(new ProximityIntentReceiver(), filter);
        }
    }

    private void removeProximityAlert(){
        Intent proximityIntent = new Intent(PROX_ALERT_INTENT);
      //  PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, proximityIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
       // manager.removeProximityAlert(pendingIntent);
    }

    @Override
    public void onBackPressed(){
        Bundle bundle = new Bundle();
        bundle.putString("reminderText", reminderText);
        bundle.putLong("reminderID", reminder.getId());
        //   bundle.putBoolean("newReminder", !editMode);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.details, menu);
        return true;
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
}
