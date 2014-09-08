package com.touchdown.app.smartassistant.views;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.touchdown.app.smartassistant.R;
import com.touchdown.app.smartassistant.models.Task;

import java.util.List;

/**
 * Created by Pete on 8.9.2014.
 */
public class TaskArrayAdapter extends ArrayAdapter<Task> {

    private int layoutResId;
    private List<Task> tasks;
    private Context context;

    public TaskArrayAdapter(Context context, int resource, List<Task> tasks) {
        super(context, resource, tasks);
        this.layoutResId = resource;
        this.tasks = tasks;
        this.context = context;
    }

    public void updateData(List<Task> tasks){
        clear();
        if(tasks != null){
            this.tasks = tasks;
            for(int i = 0; i < this.tasks.size(); i++){
                insert(this.tasks.get(i), i);
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TaskHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResId, parent, false);

            holder = new TaskHolder();

            holder.taskName = (TextView) row.findViewById(R.id.itemText);
            holder.box = (CheckBox) row.findViewById(R.id.checkBox);

            row.setTag(holder);
        } else {
            holder = (TaskHolder) row.getTag();
        }

        Task task = tasks.get(position);

        holder.taskName.setText(task.getName());
        return row;
    }

    private static class TaskHolder{
        TextView taskName;
        CheckBox box;
    }
}
