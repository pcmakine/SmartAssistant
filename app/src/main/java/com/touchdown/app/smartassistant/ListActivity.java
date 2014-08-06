package com.touchdown.app.smartassistant;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.touchdown.app.smartassistant.data.DbContract;
import com.touchdown.app.smartassistant.data.DbHelper;
import com.touchdown.app.smartassistant.models.ReminderDao;


public class ListActivity extends ActionBarActivity {
    public static final String LOG_TAG = ListActivity.class.getSimpleName();
    private SimpleCursorAdapter adapter;
    private ListView listView;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listView = (ListView) findViewById(R.id.list);

        dbHelper = new DbHelper(this);

        Util.clearAndInsertTestData(dbHelper, this);

        Cursor cursor =  ReminderDao.getAll(dbHelper);

        Log.d(LOG_TAG, String.valueOf(cursor.getCount()));

        adapter = new SimpleCursorAdapter(this, R.layout.item_layout,cursor,
                new String[] {DbContract.ReminderEntry.COLUMN_CONTENT}, new int[] {R.id.itemText},
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // ListView Clicked item index
                int itemPosition     = position;

                // ListView Clicked item value
                Cursor  c    = (Cursor) listView.getItemAtPosition(position);
                c.moveToPosition(position);
                String content = c.getString(c.getColumnIndex(DbContract.ReminderEntry.COLUMN_CONTENT));

                toggleCheckBox(position);

                // adapter.notifyDataSetChanged();

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Position :"+itemPosition+"  ListItem : " + content , Toast.LENGTH_LONG)
                        .show();
            }

        });
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
                for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                    if (checked.get(i)) {
                        cursor = (Cursor) adapter.getItem(i);
                        int idIndex = cursor.getColumnIndex(DbContract.ReminderEntry._ID);
                        long reminderId = cursor.getLong(idIndex);

                        ReminderDao.remove(dbHelper, reminderId);


                        listView.setItemChecked(i, false);
                        toggleCheckBox(i);
                    }
                }
                Cursor newCursor = ReminderDao.getAll(dbHelper);

                adapter.swapCursor(newCursor);
                return true;
        }

        return false;
        //return super.onOptionsItemSelected(item);
    }
}
