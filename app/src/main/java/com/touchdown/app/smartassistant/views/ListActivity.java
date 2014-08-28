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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.touchdown.app.smartassistant.R;
import com.touchdown.app.smartassistant.services.TaskActivator;
import com.touchdown.app.smartassistant.services.TaskActivatorKiller;
import com.touchdown.app.smartassistant.services.Util;
import com.touchdown.app.smartassistant.data.AsyncTasks.RemoveTasksListener;
import com.touchdown.app.smartassistant.data.AsyncTasks.RemoveTasksTask;
import com.touchdown.app.smartassistant.data.DbContract;
import com.touchdown.app.smartassistant.data.DbHelper;
import com.touchdown.app.smartassistant.data.AsyncTasks.FetchAllDataListener;
import com.touchdown.app.smartassistant.data.AsyncTasks.FetchAllDataTask;

import java.util.ArrayList;
import java.util.List;


public class ListActivity extends ActionBarActivity implements FetchAllDataListener, RemoveTasksListener {
    public static final String LOG_TAG = ListActivity.class.getSimpleName();
    private SimpleCursorAdapter adapter;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listView = (ListView) findViewById(R.id.list);

  //      Util.clearAndInsertTestData(this, new DbHelper(this));

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                Cursor c = (Cursor) listView.getItemAtPosition(position);
                c.moveToPosition(position);
                String name = c.getString(c.getColumnIndex(DbContract.TaskEntry.COLUMN_NAME_TASK_NAME));

                toggleCheckBox(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Position :" + itemPosition + "  ListItem : " + name, Toast.LENGTH_LONG)
                        .show();
            }

        });
    }

    private void getAllTasks(boolean showIndicator){
        new FetchAllDataTask(this, showIndicator).execute(this);
    }

    //Callback that is called from the database tasks
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
    }

    @Override
    public LinearLayout getOnProgressIndicator() {
        return (LinearLayout) findViewById(R.id.onProgressIndicator);
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
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    public void startEdit(long reminderId){
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("reminderID", reminderId);
        startActivity(intent);
    }

    @Override
    public void onResume(){
        //updateList();
        super.onResume();
        getAllTasks(true);
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
                Cursor cursor;
                List<Long> idList = new ArrayList<Long>();
                for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                    if (checked.get(i)) {
                        cursor = (Cursor) adapter.getItem(i);
                        int idIndex = cursor.getColumnIndex(DbContract.TaskEntry._ID);
                        long taskId = cursor.getLong(idIndex);
                        idList.add(taskId);

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
                            cursor = (Cursor) adapter.getItem(i);
                            int idIndex = cursor.getColumnIndex(DbContract.TaskEntry._ID);
                            long taskId = cursor.getLong(idIndex);
                            startEdit(taskId);

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
        Toast.makeText(this, "Task(s) deleted successfully", Toast.LENGTH_SHORT).show();
        getAllTasks(false);
    }
}
