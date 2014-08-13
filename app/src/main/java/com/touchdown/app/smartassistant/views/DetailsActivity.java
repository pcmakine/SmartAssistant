package com.touchdown.app.smartassistant.views;

import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.touchdown.app.smartassistant.R;
import com.touchdown.app.smartassistant.data.DbHelper;
import com.touchdown.app.smartassistant.models.LocationDao;
import com.touchdown.app.smartassistant.models.ReminderDao;
import com.touchdown.app.smartassistant.services.GetAddressTask;
import com.touchdown.app.smartassistant.services.ProximityAlarmManager;

import java.util.Calendar;


public class DetailsActivity extends ActionBarActivity {
    public static final String LOG_TAG = DetailsActivity.class.getSimpleName();

    private static final int MIN_RADIUS_METERS = 50;
    private static final int MAX_RADIUS_METERS = 10000;
    private static final int SEEKBAR_MULTIPLIER_BELOW_KILOMETER = 10;
    private static final int SEEKBAR_MULTIPLIER_OVER_KILOMETER = 100;
    private static final int SEEKBAR_MULTIPLIER_CHANGE_TRESHOLD = 1000;

    private SQLiteOpenHelper dbHelper;
    private LatLng location;
    private String reminderText;
    private SeekBar radiusBar;
    private int radius;

    private AsyncTask<LatLng, Void, String> addressTask;
    private ProgressBar activityIndicator;
    private CountDownTimer timer;
    private boolean onCreateHasRun;

    private TextView contentToSaveTW;
    private TextView radiusTW;
    private CompoundButton onSwitch;
    private TextView locationText;
    private ReminderDao reminder;
    private boolean editMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG_TAG, "oncreate called");
        setContentView(R.layout.activity_details);
        dbHelper = new DbHelper(this);

        locationText = (TextView) findViewById(R.id.location);
        locationText.setText("");
        this.activityIndicator = (ProgressBar) findViewById(R.id.address_progress);
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
        fetchAddress();
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
        reminder.setOn(true);
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

    private void fetchAddress(){
        if(Geocoder.isPresent() && location != null){
            this.addressTask = new GetAddressTask(DetailsActivity.this);
            this.addressTask.execute(location);
            this.activityIndicator.setVisibility(View.VISIBLE);
            startAddressTaskExpirationTimer();
        }
    }

    private void startAddressTaskExpirationTimer(){
        timer = new CountDownTimer(GetAddressTask.TASK_EXPIRATION_SECS * 1000, GetAddressTask.TASK_EXPIRATION_SECS * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                finishFetchingAddress();
                locationText.setText(getResources().getString(R.string.error_address_could_not_be_found));
                Log.d(LOG_TAG, Calendar.getInstance().getTime() + "");
            }
        }.start();
    }

    private int metersToProgress(int meters){
        if(meters <= SEEKBAR_MULTIPLIER_CHANGE_TRESHOLD){
            return meters/SEEKBAR_MULTIPLIER_BELOW_KILOMETER - MIN_RADIUS_METERS/SEEKBAR_MULTIPLIER_BELOW_KILOMETER;
        }
        return metersToProgress(SEEKBAR_MULTIPLIER_CHANGE_TRESHOLD) +
                (meters - SEEKBAR_MULTIPLIER_CHANGE_TRESHOLD) / SEEKBAR_MULTIPLIER_OVER_KILOMETER;

    }


    private void setUpSeekBar(){
        radiusBar = (SeekBar)findViewById(R.id.seekBar);
        if(reminder.getLocation() != null){
            int rad = reminder.getLocation().getRadius();
            radiusBar.setProgress(metersToProgress(rad));
        }

        radiusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int progressMultiplierTreshold = metersToProgress(SEEKBAR_MULTIPLIER_CHANGE_TRESHOLD); //SEEKBAR_MULTIPLIER_CHANGE_TRESHOLD/SEEKBAR_MULTIPLIER_BELOW_KILOMETER - MIN_RADIUS_METERS;

                if(progress < progressMultiplierTreshold){
                    radius = progress * SEEKBAR_MULTIPLIER_BELOW_KILOMETER + MIN_RADIUS_METERS;
                }else{
                    radius = SEEKBAR_MULTIPLIER_CHANGE_TRESHOLD + (progress - progressMultiplierTreshold) * SEEKBAR_MULTIPLIER_OVER_KILOMETER;
                }
                radiusTW.setText(getRadiusText());
                if(reminder.getLocation() != null){
                    reminder.getLocation().setRadius(radius);
                }
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
        int progressUntilTreshold = SEEKBAR_MULTIPLIER_CHANGE_TRESHOLD / SEEKBAR_MULTIPLIER_BELOW_KILOMETER;
        int progressFromTresholdToMaxRadius = (MAX_RADIUS_METERS - SEEKBAR_MULTIPLIER_CHANGE_TRESHOLD) / SEEKBAR_MULTIPLIER_OVER_KILOMETER;
        int maxValue = progressUntilTreshold + progressFromTresholdToMaxRadius - MIN_RADIUS_METERS / SEEKBAR_MULTIPLIER_BELOW_KILOMETER;
        radiusBar.setMax(maxValue);
    }

    private void setUpCompoundButton(){
        onSwitch = (CompoundButton) findViewById(R.id.myswitch);
        onSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    reminder.setOn(true);
                }else{
                    reminder.setOn(false);
                }
            }
        });
    }

    public void addOrUpdate(View view){
        reminderText  = contentToSaveTW.getText().toString();
        reminder.setContent(reminderText);
        if(!displayErrorToastOnEmptyReminder(reminderText)){
            if(editMode){
                updateReminder();
            }else{
                addReminder();
            }
        }
    }

    private boolean displayErrorToastOnEmptyReminder(String reminderText){
        if(reminderText.equals("")){
            Toast.makeText(this, R.string.erro_no_reminder_text_entered, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void updateReminder(){
        if(reminder.update(dbHelper)){
            if(onSwitch.isChecked()){
                ProximityAlarmManager.updateAlert(reminder);
            }else{
                 ProximityAlarmManager.removeAlert(reminder);
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
            ProximityAlarmManager.saveAlert(reminder);
            Toast toast = Toast.makeText(this, R.string.successfully_added, Toast.LENGTH_LONG);
            toast.show();
            onBackPressed();
        }else{
            Toast toast = Toast.makeText(this, R.string.reminder_not_added, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void deliverAddress(String address){
        this.locationText.setText(address);
        this.activityIndicator.setVisibility(View.GONE);
        this.timer.cancel();
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
    public void onResume(){
        super.onResume();
        if((locationText.getText().equals("") ||
                locationText.getText().equals(getResources().getString(R.string.error_address_could_not_be_found)))
                && addressTask.getStatus() != AsyncTask.Status.RUNNING){
            fetchAddress();
        }
    }

    private void finishFetchingAddress(){
        this.addressTask.cancel(true);
        this.activityIndicator.setVisibility(View.GONE);
    }


    @Override
    public void onPause(){
        super.onPause();
        finishFetchingAddress();
        timer.cancel();
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
