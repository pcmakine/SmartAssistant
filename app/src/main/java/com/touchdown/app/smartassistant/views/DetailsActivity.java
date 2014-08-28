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
import com.touchdown.app.smartassistant.models.Task;
import com.touchdown.app.smartassistant.models.TriggerLocation;
import com.touchdown.app.smartassistant.services.PendingTask;


public class DetailsActivity extends ActionBarActivity implements AlarmFragment.OnFragmentInteractionListener,
        UpdateTaskListener, FetchOneTaskListener, LocationFragment.onFragmentInteractionListener {
    public static final String LOG_TAG = DetailsActivity.class.getSimpleName();

    private LatLng location;
    private String nameInput;
    private TextView nameTw;

    private Task task;
    private boolean editMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG_TAG, "oncreate called");

        Intent intent = getIntent();
        if(noReminderIdInExtras(intent)){
            makeNewReminder();
        }else{
            long id = intent.getLongExtra("reminderID", -1);
            new FetchOneTaskTask(this).execute(id);
        }
    }

    private void showUi(){
        setContentView(R.layout.activity_details);
        nameTw = (TextView) findViewById(R.id.contentToSave);
    }

    private void prepareTaskRelatedUiElements(){
        addLocationFragment();
        addAlarmFragment();
     //   setUpSeekBar();
      //  setUpCheckBoxes();
    }

    private boolean noReminderIdInExtras(Intent intent){
        return intent.getLongExtra("reminderID", -1) == -1;
    }

    private void makeNewReminder(){
        showUi();
        this.location = getIntent().getParcelableExtra("location");
        if(location != null){
            this.task = new Task(-1, "",
                    new TriggerLocation(-1, location, TriggerLocation.DEFAULT_RADIUS, -1),
                    new NotificationReminder(-1, 0, "", true, -1));
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

            fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit);

            locationFragment = LocationFragment.createFragment(task.getLocation());

            fragmentTransaction.add(R.id.locationFragmentContainer, locationFragment, "HELLO");
            fragmentTransaction.commit();

        }
    }

    public void addAlarmFragment(){
        FragmentManager fManager = getSupportFragmentManager();
        Fragment alarmFragment = fManager.findFragmentById(R.id.fragment_container);

        if(alarmFragment != null && alarmFragment.isInLayout()){
            //dont do anything
        }else{
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit);

            alarmFragment = AlarmFragment.createFragment(task.getAlarm());

            fragmentTransaction.add(R.id.fragment_container, alarmFragment, "HELLO");
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
            showSuccessMessage();
            onBackPressed();
        }else{
            showErrorMessage();
        }
    }

    private void showSuccessMessage(){
        if(editMode){
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
    public void onFragmentInteraction(NotificationReminder alarm) {
        task.addAction(alarm);
    }

    @Override
    public void onFragmentInteraction(TriggerLocation location) {
        task.setLocation(location);
    }
}

