package com.touchdown.app.smartassistant.views;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.touchdown.app.smartassistant.R;
import com.touchdown.app.smartassistant.newdb.NotificationReminder;

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
    public static final String LOG_TAG = DetailsActivity.class.getSimpleName();

    private OnFragmentInteractionListener mListener;
    private CompoundButton onSwitch;
    private NotificationReminder alarm;

    public static AlarmFragment createFragment(NotificationReminder alarm) {
        AlarmFragment fragment = new AlarmFragment();
        Bundle args = new Bundle();
        args.putSerializable("alarm", alarm);
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
            alarm = (NotificationReminder) getArguments().getSerializable("alarm");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      //  alarm = (ActionReminder) getArguments().getSerializable("alarm");

        // Inflate the layout for this fragment
        LinearLayout ll =  (LinearLayout) inflater.inflate(R.layout.fragment_alarm, container, false);
        onSwitch = (CompoundButton) ll.findViewById(R.id.myswitch);
        setUpCompoundButton();
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

    // TODO: Rename method, update argument and hook method into UI event
    public void passUpdatedReminderToParentActivity(NotificationReminder reminder) {
        if (mListener != null) {
            mListener.onFragmentInteraction(reminder);
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
        public void onFragmentInteraction(NotificationReminder reminder);
    }

}
