package com.touchdown.app.smartassistant.views;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;

import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.touchdown.app.smartassistant.R;
import com.touchdown.app.smartassistant.data.asyncTasks.FetchAllTasksListener;
import com.touchdown.app.smartassistant.data.asyncTasks.FetchAllTasksTask;
import com.touchdown.app.smartassistant.data.asyncTasks.RemoveTasksListener;
import com.touchdown.app.smartassistant.data.asyncTasks.RemoveTasksTask;
import com.touchdown.app.smartassistant.data.DbContract;
import com.touchdown.app.smartassistant.data.asyncTasks.FetchAllDataListener;
import com.touchdown.app.smartassistant.models.Task;
import com.touchdown.app.smartassistant.services.Common;

import java.util.ArrayList;
import java.util.List;


public class ListActivity extends ActionBarActivity implements RemoveTasksListener, FetchAllTasksListener {
    public static final String LOG_TAG = ListActivity.class.getSimpleName();
    private ListView listView;
    private TaskArrayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listView = (ListView) findViewById(R.id.list);

        //Util.clearDb(this, DbHelper.getInstance(this));
  //      Util.clearAndInsertTestData(this, new DbHelper(this));

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                Task task = (Task) listView.getItemAtPosition(position);

                String name = task.getName();

                toggleCheckBox(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Position :" + itemPosition + "  ListItem : " + name, Toast.LENGTH_LONG)
                        .show();
            }

        });
    }

    private void getAllTasks(){

        changeProgressIndicatorVisibility(View.VISIBLE);
        new FetchAllTasksTask(this).execute(this);
    }

    private void toggleCheckBox(int position){
        SparseBooleanArray checked = listView.getCheckedItemPositions();
        int firstPos = listView.getFirstVisiblePosition();
        RelativeLayout layout = (RelativeLayout) listView.getChildAt(position - firstPos);
        CheckBox checkBox = (CheckBox) layout.findViewById(R.id.checkBox);
        if(checked.get(position)){
            checkBox.setChecked(true);
        }else{
            checkBox.setChecked(false);
        }
    }

    public void viewMap(){
        Intent intent = new Intent(this, Map.class);
        startActivity(intent);
    }

    public void startEdit(Task task){
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(Common.TASK_TAG, task);
        startActivity(intent);
    }

    @Override
    public void onResume(){
        //updateList();
        super.onResume();
        getAllTasks();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.action_map:
                viewMap();
                return true;
            case R.id.action_delete:
                SparseBooleanArray checked = listView.getCheckedItemPositions();
                Task task;
                List<Long> idList = new ArrayList<Long>();
                for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                    if (checked.get(i)) {
                        task = (Task) mAdapter.getItem(i);
                        idList.add(task.getId());

                        listView.setItemChecked(i, false);
                        toggleCheckBox(i);
                    }
                }

                new RemoveTasksTask(this).execute(idList);
               // getAllTasks(false);
                return true;

            case R.id.action_select_all:
                checked = listView.getCheckedItemPositions();
                boolean allSelected = true;
                for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                    if(!checked.get(i)){
                        allSelected = false;
                        listView.setItemChecked(i, true);
                        toggleCheckBox(i);
                    }
                }
                if(allSelected){
                    for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                        listView.setItemChecked(i, false);
                        toggleCheckBox(i);
                    }
                }
                return true;
            case R.id.action_edit:
                checked = listView.getCheckedItemPositions();
                if(listView.getCheckedItemCount() == 1){
                    for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                        if (checked.get(i)) {
                            task = mAdapter.getItem(i);
                            startEdit(task);

                        }
                    }

                }else if(listView.getCheckedItemCount() < 1){
                    Toast.makeText(this, R.string.error_choose_one_reminder, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, R.string.error_choose_only_one_reminder, Toast.LENGTH_SHORT).show();
                }
        }
        return false;
        //return super.onOptionsItemSelected(item);
    }

    @Override
    public void removeSuccessful(boolean success) {
        if(success){
            Toast.makeText(this, "Task(s) deleted successfully", Toast.LENGTH_SHORT).show();
        }
        getAllTasks();
    }

    private void changeProgressIndicatorVisibility(int visibility){
        LinearLayout progressInd = (LinearLayout) findViewById(R.id.onProgressIndicator);
        progressInd.setVisibility(visibility);
    }

    //Callback that is called when the tasks have been fetched from the database
    @Override
    public void update(List<Task> taskList) {
        changeProgressIndicatorVisibility(View.GONE);

        if(mAdapter == null){
            mAdapter = new TaskArrayAdapter(this, R.layout.item_layout, taskList);
            listView.setAdapter(mAdapter);
        }else{
            mAdapter.updateData(taskList);
            mAdapter.notifyDataSetChanged();
        }
    }

/*    //Callback that is called from the database tasks
    @Override
    public void updateData(Cursor cursor) {
        if(adapter == null){
            adapter = new SimpleCursorAdapter(this, R.layout.item_layout,cursor,
                    new String[] {DbContract.TaskEntry.COLUMN_NAME_TASK_NAME}, new int[] {R.id.itemText},
                    SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            listView.setAdapter(adapter);
        }else{
            adapter.swapCursor(cursor);
        }
    }*/
}
