package com.touchdown.app.smartassistant.views;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.touchdown.app.smartassistant.R;
import com.touchdown.app.smartassistant.models.TriggerLocation;

public class LocationFragment extends Fragment {
    public static final String LOG_TAG = DetailsActivity.class.getSimpleName();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARGS_LOCATION = "location";
    private static final int MIN_RADIUS_METERS = 50;
    private static final int MAX_RADIUS_METERS = 10000;
    private static final int SEEKBAR_MULTIPLIER_BELOW_KILOMETER = 10;
    private static final int SEEKBAR_MULTIPLIER_OVER_KILOMETER = 100;
    private static final int SEEKBAR_MULTIPLIER_CHANGE_TRESHOLD = 1000;

    private TriggerLocation location;

    private RadioButton entering;
    private RadioButton exiting;
    private SeekBar radiusBar;
    private int radius;
    private TextView radiusTW;

    private onFragmentInteractionListener mListener;

    public static LocationFragment createFragment(TriggerLocation location) {
        LocationFragment fragment = new LocationFragment();

        Bundle args  = new Bundle();
        args.putParcelable(ARGS_LOCATION, location);
        fragment.setArguments(args);
        return fragment;
    }

    public LocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            location = getArguments().getParcelable("location");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout ll =  (LinearLayout) inflater.inflate(R.layout.fragment_location, container, false);
        entering = (RadioButton) ll.findViewById(R.id.enteringRadio);
        exiting = (RadioButton) ll.findViewById(R.id.exitingRadio);
        radiusBar = (SeekBar) ll.findViewById(R.id.seekBar);
        radiusTW = (TextView) ll.findViewById(R.id.radius);

        setUpRadios();
        setUpSeekBar();

        return ll;
    }

        private void setUpRadios(){
            entering.setChecked(location.isArrivalTriggerOn());
            setListenerForRadios(entering);

            exiting.setChecked(location.isDepartureTriggerOn());
            setListenerForRadios(exiting);
        }

        private void setListenerForRadios(RadioButton btn){
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRadioButtonClicked(v);
                }
            });
        }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.enteringRadio:
                if (checked){
                    location.turnOnArrivalTrigger();
                    location.turnOffDepartureTrigger();
                    exiting.setChecked(false);
                }
                break;
            case R.id.exitingRadio:
                if (checked){
                    location.turnOffArrivalTrigger();
                    entering.setChecked(false);
                    location.turnOnDepartureTrigger();
                }
                break;
        }
    }


    private void setUpSeekBar(){
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
                if(location != null){
                    location.setRadius(radius);
                }
                mListener.onFragmentInteraction(location);
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

    private int metersToProgress(int meters){
        if(meters <= SEEKBAR_MULTIPLIER_CHANGE_TRESHOLD){
            return meters/SEEKBAR_MULTIPLIER_BELOW_KILOMETER - MIN_RADIUS_METERS/SEEKBAR_MULTIPLIER_BELOW_KILOMETER;
        }
        return metersToProgress(SEEKBAR_MULTIPLIER_CHANGE_TRESHOLD) +
                (meters - SEEKBAR_MULTIPLIER_CHANGE_TRESHOLD) / SEEKBAR_MULTIPLIER_OVER_KILOMETER;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (onFragmentInteractionListener) activity;
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
    public interface onFragmentInteractionListener {
        public void onFragmentInteraction(TriggerLocation location);
    }

}
