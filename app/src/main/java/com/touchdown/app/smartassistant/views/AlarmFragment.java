package com.touchdown.app.smartassistant.views;

import android.app.Activity;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AlarmFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AlarmFragment#createFragment} factory method to
 * create an instance of this fragment.
 *
 */
public class AlarmFragment extends Fragment {
    public static final String LOG_TAG = AlarmFragment.class.getSimpleName();
    private static final String ALARM_BUNDLE_KEY = "alarm";

    private OnFragmentInteractionListener mListener;
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

    private void setUpCompoundButton(){
        onSwitch.setChecked(alarm.isOn());
        onSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    alarm.turnOn();
                }else{
                    alarm.turnOff();
                }
                passUpdatedReminderToParentActivity(alarm);
            }
        });
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


    public void passUpdatedReminderToParentActivity(Alarm alarm) {
        if (mListener != null) {
            mListener.onFragmentInteraction(alarm);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Alarm alarm);
    }

}
