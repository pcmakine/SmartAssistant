package com.touchdown.app.smartassistant.views;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.touchdown.app.smartassistant.R;
import com.touchdown.app.smartassistant.data.asyncTasks.FetchOneTaskListener;
import com.touchdown.app.smartassistant.data.asyncTasks.FetchOneTaskTask;
import com.touchdown.app.smartassistant.data.asyncTasks.UpdateTaskListener;
import com.touchdown.app.smartassistant.data.asyncTasks.UpdateTaskTask;
import com.touchdown.app.smartassistant.models.Action;
import com.touchdown.app.smartassistant.models.Alarm;
import com.touchdown.app.smartassistant.models.RingerVolume;
import com.touchdown.app.smartassistant.models.Task;
import com.touchdown.app.smartassistant.models.TriggerLocation;
import com.touchdown.app.smartassistant.services.Common;
import com.touchdown.app.smartassistant.services.PendingTask;


public class DetailsActivity extends ActionBarActivity implements UpdateTaskListener,
        FetchOneTaskListener, LocationFragment.onFragmentInteractionListener, OnActionFragmentInteractionListener {
    public static final String LOG_TAG = DetailsActivity.class.getSimpleName();

    private LatLng location;
    private String nameInput;
    private TextView nameTw;

    private Task task;
    private boolean editMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showUi();

        Intent intent = getIntent();
        task = intent.getParcelableExtra(Common.TASK_TAG);
        if(task == null){
            createNewTask();
        }else{
            useExistingReminder(task);
        }
        Log.e(LOG_TAG, "oncreate called");

    }

    private void showUi(){
        setContentView(R.layout.activity_details);
        nameTw = (TextView) findViewById(R.id.contentToSave);
    }

    private void prepareTaskRelatedUiElements(){
        addLocationFragment();
        addAlarmFragment();
        addRingerVolumeFragment();
    }

    private void createNewTask(){
        showUi();
        this.location = getIntent().getParcelableExtra("location");
        if(location != null){
            this.task = new Task(-1, "",
                    TriggerLocation.createDefault(location));
            task.addAction(Alarm.createDefault());
            task.addAction(RingerVolume.createDefault());
        }else{
        }
        editMode = false;
        prepareTaskRelatedUiElements();
    }

    private void useExistingReminder(Task task){
        this.task = task;
        this.location = ((TriggerLocation) task.getTrigger()).getLatLng();
        nameTw.setText(task.getName());
        editMode = true;
        prepareTaskRelatedUiElements();
    }

    public void addOrUpdate(View view){
        nameInput = nameTw.getText().toString();
        task.setName(nameInput);
        if(!displayErrorToastOnEmptyName(nameInput) && !displayErrorToastOnNoActions()){
            task = PendingTask.updatePendingStatus(task);
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
        new UpdateTaskTask(this, false,false).execute(task);    //second false for updatetask
    }

    private void addTask(){

        new UpdateTaskTask(this, false, true).execute(task);                  //true for insert task
    }

    public void addLocationFragment(){
        FragmentManager fManager = getSupportFragmentManager();
        Fragment locationFragment = fManager.findFragmentById(R.id.locationFragmentContainer);

        if(locationFragment != null && locationFragment.isInLayout()){
            //dont do anything
        }else{
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.setCustomAnimations(R.anim.pop_out, R.anim.exit);

            locationFragment = LocationFragment.createFragment(task.getLocation());

            fragmentTransaction.add(R.id.locationFragmentContainer, locationFragment, "HELLO");
            fragmentTransaction.commit();

        }
    }

    public void addAlarmFragment(){
        FragmentManager fManager = getSupportFragmentManager();
        Fragment alarmFragment = fManager.findFragmentById(R.id.alarm_fragment_container);

        if(alarmFragment != null && alarmFragment.isInLayout()){
            //dont do anything
        }else{
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.setCustomAnimations(R.anim.pop_out, R.anim.exit);

            alarmFragment = AlarmFragment.createFragment(task.getAlarm());

            fragmentTransaction.add(R.id.alarm_fragment_container, alarmFragment, "HELLO");
            fragmentTransaction.commit();

        }
    }

    public void addRingerVolumeFragment(){
        FragmentManager fManager = getSupportFragmentManager();
        Fragment rVolumeFragment = fManager.findFragmentById(R.id.ringerVolume_fragment_container);

        if(rVolumeFragment != null && rVolumeFragment.isInLayout()){
            //dont do anything
        }else{
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.setCustomAnimations(R.anim.pop_out, R.anim.exit);

            rVolumeFragment = RingerVolumeFragment.createFragment(task.getRingerVolume());

            fragmentTransaction.add(R.id.ringerVolume_fragment_container, rVolumeFragment, "HELLO");
            fragmentTransaction.commit();

        }
    }

    @Override
    public void onBackPressed(){
        Bundle bundle = new Bundle();
        bundle.putString("reminderText", nameInput);
        bundle.putLong("reminderID", task.getId());
        Intent intent = new Intent();
        intent.putExtras(bundle);
        super.onBackPressed();
    }

    @Override
    public void onResume(){

        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
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
    public void updateSuccessful(boolean success) {
        if(success){
            Log.d(LOG_TAG, "Update successful");
         //   showSuccessMessage();
            onBackPressed();
        }else{
            Log.d(LOG_TAG, "Something went wrong, not updated");
          //  showErrorMessage();
        }
    }

    private void showSuccessMessage(){
        if(editMode){
            Log.d(LOG_TAG, "Update successful");
            Toast.makeText(this, R.string.update_success, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, R.string.successfully_added, Toast.LENGTH_LONG).show();
        }
    }

    private void showErrorMessage(){
        if(editMode){
            Toast.makeText(this, R.string.update_error, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, R.string.reminder_not_added, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public LinearLayout getOnProgressIndicator() {
        return null;
    }

    @Override
    public void deliverTask(Task task) {
        showUi();
        useExistingReminder(task);
    }

    @Override
    public void onFragmentInteraction(TriggerLocation location) {
        task.setLocation(location);
    }

    @Override
    public void onFragmentInteraction(Action action) {
        task.addAction(action);
    }
}

