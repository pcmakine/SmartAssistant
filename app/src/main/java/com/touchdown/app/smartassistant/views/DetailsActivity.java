package com.touchdown.app.smartassistant.views;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.touchdown.app.smartassistant.R;
import com.touchdown.app.smartassistant.newdb.NotificationReminder;
import com.touchdown.app.smartassistant.newdb.Task;
import com.touchdown.app.smartassistant.newdb.TaskManager;
import com.touchdown.app.smartassistant.newdb.TriggerLocation;


public class DetailsActivity extends ActionBarActivity implements AlarmFragment.OnFragmentInteractionListener {
    public static final String LOG_TAG = DetailsActivity.class.getSimpleName();

    private static final int MIN_RADIUS_METERS = 50;
    private static final int MAX_RADIUS_METERS = 10000;
    private static final int SEEKBAR_MULTIPLIER_BELOW_KILOMETER = 10;
    private static final int SEEKBAR_MULTIPLIER_OVER_KILOMETER = 100;
    private static final int SEEKBAR_MULTIPLIER_CHANGE_TRESHOLD = 1000;

    private LatLng location;
    private String nameInput;
    private SeekBar radiusBar;
    private int radius;

    private CheckBox entering;
    private CheckBox leaving;

    private AsyncTask<LatLng, Void, String> addressTask;
    private ProgressBar activityIndicator;
    private CountDownTimer timer;

    private TextView nameTw;
    private TextView radiusTW;
    private CompoundButton onSwitch;
    private TextView locationText;
    private Task task;
    private boolean editMode;

    private TaskManager taskManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG_TAG, "oncreate called");
        setContentView(R.layout.activity_details);
        taskManager = TaskManager.getInstance(this);

        //       locationText = (TextView) findViewById(R.id.location);
        //       locationText.setText("");
        //   this.activityIndicator = (ProgressBar) findViewById(R.id.address_progress);
        nameTw = (TextView) findViewById(R.id.contentToSave);
        radiusTW = (TextView) findViewById(R.id.radius);

        Intent intent = getIntent();
        if(noReminderIdInExtras(intent)){
            makeNewReminder();
        }else{
            long id = intent.getLongExtra("reminderID", -1);
            Task task = taskManager.findTaskById(id);
            useExistingReminder(task);
        }
        //      fetchAddress();
        setUpSeekBar();
        if(savedInstanceState == null){
            addAlarmFragment();
        }
        setUpCheckBoxes();
        // setUpSpinner();
    }

    private void setUpCheckBoxes(){
        TriggerLocation loc = task.getLocation();
        entering = (CheckBox) findViewById(R.id.enteringCheckBox);
        entering.setChecked(loc.isArrivalTriggerOn());
        setListenerForEnteringCheckBox();

        leaving = (CheckBox) findViewById(R.id.leavingCheckBox);
        leaving.setChecked(loc.isDepartureTriggerOn());
        setListenerForLeavingCheckBox();
    }

    private void setListenerForEnteringCheckBox(){
        entering.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(LOG_TAG, "entering checkbox checked value: " + isChecked);
                TriggerLocation loc = task.getLocation();
                if(isChecked){
                    loc.turnOnArrivalTrigger();
                }else{
                    loc.turnOffArrivalTrigger();
                }
            }
        });
    }

    private void setListenerForLeavingCheckBox(){
        leaving.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(LOG_TAG, "leaving checkbox checked value: " + isChecked);
                TriggerLocation loc = task.getLocation();
                if(isChecked){
                    loc.turnOnDepartureTrigger();
                }else{
                    loc.turnOffDepartureTrigger();
                }
            }
        });
    }

    private void setUpSpinner(){
/*        actionPicker = (Spinner) findViewById(R.id.action_picker);

        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.actions_array, android.R.layout.simple_spinner_item);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        actionPicker.setAdapter(spinnerAdapter);*/
    }

    private boolean noReminderIdInExtras(Intent intent){
        return intent.getLongExtra("reminderID", -1) == -1;
    }

    private void makeNewReminder(){
        this.location = getIntent().getParcelableExtra("location");
        if(location != null){
            this.task = new Task(-1, "", new TriggerLocation(-1, location, radius, -1), new NotificationReminder(-1, 0, "", true, -1));
        }else{
          //  this.task = new Task(-1, "", null, null);
        }
        //   task.setOn(true);
        //  onSwitch.setChecked(true);
        editMode = false;
    }

    private void useExistingReminder(Task task){
        this.task = task;
        this.location = ((TriggerLocation) task.getTrigger()).getLatLng();
        nameTw.setText(task.getName());
        // onSwitch.setChecked(reminder.isOn());
        editMode = true;
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
        TriggerLocation location = (TriggerLocation) task.getTrigger();
        if(location != null){
            int rad = location.getRadius();
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
                TriggerLocation location = (TriggerLocation) task.getTrigger();
                if(location != null){
                    location.setRadius(radius);
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

    public void addOrUpdate(View view){
        nameInput = nameTw.getText().toString();
        task.setName(nameInput);
        if(!displayErrorToastOnEmptyName(nameInput) && !displayErrorToastOnNoActions()){
            if(editMode){
                updateTask();
            }else{
                addTask();
            }
        }
    }

    private boolean displayErrorToastOnEmptyName(String reminderText){
        if(reminderText.equals("")){
            Toast.makeText(this, R.string.erro_no_reminder_text_entered, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private boolean displayErrorToastOnNoActions(){
        if(task.getActions().isEmpty()){
            Toast.makeText(this, R.string.error_no_actions_added, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void updateTask(){
        if(taskManager.update(task)){
            Toast toast = Toast.makeText(this, R.string.successfully_edited, Toast.LENGTH_LONG);
            toast.show();
            onBackPressed();
        }else{
            Toast toast = Toast.makeText(this, R.string.reminder_not_edited, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void addTask(){
        if(taskManager.insert(task) != -1){
            Toast toast = Toast.makeText(this, R.string.successfully_added, Toast.LENGTH_LONG);
            toast.show();
            onBackPressed();
        }else{
            Toast toast = Toast.makeText(this, R.string.reminder_not_added, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void addAlarmFragment(){

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AlarmFragment alarmFragment = AlarmFragment.createFragment(task.getAlarm());

        //alarmFragment.setArguments(new Bundle());
        fragmentTransaction.add(R.id.fragment_container, alarmFragment, "HELLO");
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed(){
        Bundle bundle = new Bundle();
        bundle.putString("reminderText", nameInput);
        bundle.putLong("reminderID", task.getId());
        //   bundle.putBoolean("newReminder", !editMode);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        super.onBackPressed();
    }

    @Override
    public void onResume(){
        super.onResume();
 /*       if((locationText.getText().equals("") ||
                locationText.getText().equals(getResources().getString(R.string.error_address_could_not_be_found)))
                && addressTask.getStatus() != AsyncTask.Status.RUNNING){
            fetchAddress();
        }*/
    }

    @Override
    public void onPause(){
        super.onPause();
//        finishFetchingAddress();
//        timer.cancel();
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

    @Override
    public void onFragmentInteraction(NotificationReminder alarm) {
        task.addAction(alarm);
    }
}


/*

    private void finishFetchingAddress(){
        this.addressTask.cancel(true);
        this.activityIndicator.setVisibility(View.GONE);
    }
*/

/*    public void deliverAddress(String address){
        this.locationText.setText(address);
        this.activityIndicator.setVisibility(View.GONE);
        this.timer.cancel();
    }*/
/*
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
*/

