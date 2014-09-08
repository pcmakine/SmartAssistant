package com.touchdown.app.smartassistant.views;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.touchdown.app.smartassistant.R;
import com.touchdown.app.smartassistant.models.Alarm;
import com.touchdown.app.smartassistant.services.ApplicationContextProvider;

public class AlarmFragment extends ActionFragment {
    public static final String LOG_TAG = AlarmFragment.class.getSimpleName();
    private static final String ALARM_BUNDLE_KEY = "alarm";

    private CompoundButton onSwitch;
    private Alarm alarm;
    private CheckBox fullScreenAlarm;
    private CheckBox notificationAlarm;

    public static AlarmFragment createFragment(Alarm alarm) {
        AlarmFragment fragment = new AlarmFragment();
        Bundle args = new Bundle();
        args.putParcelable(ALARM_BUNDLE_KEY, alarm);
        fragment.setArguments(args);
        return fragment;
    }
    public AlarmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            alarm = (Alarm) getArguments().getParcelable(ALARM_BUNDLE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        LinearLayout ll =  (LinearLayout) inflater.inflate(R.layout.fragment_alarm, container, false);
        onSwitch = (CompoundButton) ll.findViewById(R.id.myswitch);
        setUpCompoundButton();

        notificationAlarm = (CheckBox) ll.findViewById(R.id.notificationAlarm);
        fullScreenAlarm = (CheckBox) ll.findViewById(R.id.fullscreenAlarm);
        setUpCheckBoxes();

        return ll;
    }

    @Override
    protected void setUpCompoundButton(){
        onSwitch.setChecked(alarm.isOn());
        onSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    alarm.turnOn();
                    changeFrameColor(getResources().getColor(R.color.orange), R.id.alarmContainer);
                }else{
                    alarm.turnOff();
                    changeFrameColor(getResources().getColor(R.color.blue), R.id.alarmContainer);
                }
                mListener.onFragmentInteraction(alarm);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        if(alarm.isOn()){
            changeFrameColor(getResources().getColor(R.color.orange), R.id.alarmContainer);
        }else{
            changeFrameColor(getResources().getColor(R.color.blue), R.id.alarmContainer);
        }
    }

    private void setUpCheckBoxes(){
        notificationAlarm.setChecked(alarm.isNotificationEnabled());
        fullScreenAlarm.setChecked(alarm.isFullScreenEnabled());

        notificationAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!neitherOptionChosen(buttonView)){
                    alarm.enableNotification(isChecked);
                }
            }
        });

        fullScreenAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!neitherOptionChosen(buttonView)){
                    alarm.enableFullScreen(isChecked);
                }
            }
        });
    }

    private boolean neitherOptionChosen(CompoundButton toggled){
        if(!notificationAlarm.isChecked() && !fullScreenAlarm.isChecked()){
            toggled.setChecked(true);
            return true;
        }
        return false;
    }

}
