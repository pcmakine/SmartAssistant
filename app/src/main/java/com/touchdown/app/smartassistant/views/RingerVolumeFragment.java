package com.touchdown.app.smartassistant.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.touchdown.app.smartassistant.R;
import com.touchdown.app.smartassistant.models.RingerVolume;

public class RingerVolumeFragment extends ActionFragment {
    public static final String LOG_TAG = RingerVolumeFragment.class.getSimpleName();
    private static final String RINGERVOLUME_BUNDLE_KEY = "ringerVolume";

    private RingerVolume ringerVolume;
    private SeekBar seekBar;
    private TextView rVolTW;
    private CompoundButton onSwitch;

    public static RingerVolumeFragment createFragment(RingerVolume rVolume) {
        RingerVolumeFragment fragment = new RingerVolumeFragment();
        Bundle args = new Bundle();
        args.putParcelable(RINGERVOLUME_BUNDLE_KEY, rVolume);
        fragment.setArguments(args);
        return fragment;
    }
    public RingerVolumeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ringerVolume = (RingerVolume) getArguments().getParcelable(RINGERVOLUME_BUNDLE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.fragment_ringer_volume, container, false);

        seekBar = (SeekBar) ll.findViewById(R.id.seekBar);
        rVolTW = (TextView) ll.findViewById(R.id.ringerVolumeTw);
        TextView titleView = (TextView) ll.findViewById(R.id.actionTitle);
        setCorrectTitle(titleView);

        onSwitch = (CompoundButton) ll.findViewById(R.id.myswitch);

        setUpSeekBar();
        setUpCompoundButton();

        return ll;
    }

    private void setCorrectTitle(TextView titleView){
        titleView.setText(R.string.ringer_volume_title);
    }

    private void setUpSeekBar(){
        if(ringerVolume != null){
            int vol = ringerVolume.getVolume();
            seekBar.setProgress(vol);
            rVolTW.setText(getResources().getString(R.string.volume) +  " " + vol);
        }
        setSeekBarListener();
    }

    private void setSeekBarListener(){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ringerVolume.setVolume(progress);
                mListener.onFragmentInteraction(ringerVolume);
                if(progress == 0){
                    rVolTW.setText(getResources().getString(R.string.silent));
                }else{
                    rVolTW.setText(getResources().getString(R.string.volume) +  " " + progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void setUpCompoundButton(){
        onSwitch.setChecked(ringerVolume.isOn());

        onSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    ringerVolume.turnOn();
                    changeFrameColor(getResources().getColor(R.color.orange), R.id.ringerVolumeContainer);
                }else{
                    ringerVolume.turnOff();
                    changeFrameColor(getResources().getColor(R.color.blue), R.id.ringerVolumeContainer);
                }
                mListener.onFragmentInteraction(ringerVolume);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        if(ringerVolume.isOn()){
            changeFrameColor(getResources().getColor(R.color.orange), R.id.ringerVolumeContainer);
        }else{
            changeFrameColor(getResources().getColor(R.color.blue), R.id.ringerVolumeContainer);
        }
    }
}
