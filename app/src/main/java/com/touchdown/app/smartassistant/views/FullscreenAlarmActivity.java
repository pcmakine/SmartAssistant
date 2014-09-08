package com.touchdown.app.smartassistant.views;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.touchdown.app.smartassistant.R;
import com.touchdown.app.smartassistant.services.Common;

import java.io.IOException;

public class FullscreenAlarmActivity extends Activity {

    private MediaPlayer mMediaPlayer;
    private static final int ALARMEXPIRATION = 120; // seconds
    private CountDownTimer timer;
    private boolean stoppedByUser;
    private Button stopAlarm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setWindowFlags();
        setContentView(R.layout.activity_fullscreen_alarm);

        stopAlarm = (Button) findViewById(R.id.stopAlarm);

        playSound(this, getAlarmUri());
        setAlarmExpiration();
        setAnimation();
        setVolumeControlStream(AudioManager.STREAM_ALARM);
    }

    public void handleButtonPress(View v){
        cleanUp();
        onBackPressed();
    }

    private void setAnimation(){
        final Animation animationScale = AnimationUtils.loadAnimation(this, R.anim.scale);
        stopAlarm.startAnimation(animationScale);
    }

    private void setAlarmExpiration(){
        timer = new CountDownTimer(Common.secondsToMs(ALARMEXPIRATION), Common.secondsToMs(ALARMEXPIRATION)) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                onBackPressed();
            }
        }.start();
    }

    //http://stackoverflow.com/questions/11823259/using-flag-show-when-locked-with-disablekeyguard-in-secured-android-lock-scree
    private void setWindowFlags(){
        Window window = this.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void playSound(Context context, Uri alert) {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mMediaPlayer.start();
            }
        });
        try {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                //  audioManager.setStreamVolume(AudioManager.STREAM_ALARM, audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)/2,AudioManager.FLAG_VIBRATE);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.prepareAsync();
                //   mMediaPlayer.setVolume(1.0f, 1.0f);
 //               mMediaPlayer.start();
            }
        } catch (IOException e) {
            // System.out.println("OOPS");
        }
    }

    private void releaseMediaPlayer(){
        if(mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    //Get an alarm sound. Try for an alarm. If none set, try notification,
    //Otherwise, ringtone.
    private Uri getAlarmUri() {
        Uri alert = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
    }

    @Override
    public void onPause(){
        super.onPause();
        if (stoppedByUser){
            cleanUp();
            finish();
        }
    }

    private void cleanUp(){
        releaseMediaPlayer();
        if(timer != null){
            timer.cancel();
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        this.stoppedByUser = false;
    }

    @Override
    public void onUserLeaveHint(){
        super.onUserLeaveHint();
        this.stoppedByUser = true;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        this.stoppedByUser = true;
    }
}
