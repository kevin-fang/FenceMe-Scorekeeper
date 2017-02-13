package com.kfang.fenceme;

import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import static com.kfang.fenceme.MainActivity.mCurrentTimer;
import static com.kfang.fenceme.MainActivity.mStartTimer;


/**
 * Timer Handler
 */

public class TimerHandler extends Service {
    public static int TOGGLE_TIMER = 3;
    public static int RESET_TIMER = 4;
    public boolean timerRunning = false;
    // keeps track of the time when the timer was started
    long mStartTime = 0;
    private long mCurrentTime = 180000;
    private Ringtone mAlarmTone;
    private Handler mHandler = new Handler();
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            mCurrentTime = (int) (mCurrentTime - 1000);

            if (mCurrentTime > 0) {
                int newTime = (int) mCurrentTime / 1000;
                int seconds = newTime % 60;
                int minutes = newTime / 60;

                if (seconds < 10 && seconds >= 0) {
                    mCurrentTimer.setText("" + minutes + ":0" + seconds);
                } else {
                    mCurrentTimer.setText("" + minutes + ":" + seconds);
                }
                mHandler.postDelayed(this, 1000);
            } else if (mCurrentTime == 0) {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                mAlarmTone = RingtoneManager.getRingtone(getApplicationContext(), notification);
                mAlarmTone.play();
                mCurrentTimer.setText(R.string.no_time);
                Toast timerUp = Toast.makeText(getApplicationContext(), "Time's Up!", Toast.LENGTH_SHORT);
                timerUp.show();
            }
        }
    };
    @Override
    public IBinder onBind(Intent i) {
        return null;
    }

    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
        int toggleOrReset = i.getIntExtra("TOGGLE", TOGGLE_TIMER);
        if (toggleOrReset == TOGGLE_TIMER) {
            if (!timerRunning) {
                mStartTime = System.currentTimeMillis();
                mHandler.removeCallbacks(mUpdateTimeTask);
                mHandler.postDelayed(mUpdateTimeTask, 1000);
                mStartTimer.setText(R.string.stop_timer);
                timerRunning = true;
            } else {
                mHandler.removeCallbacks(mUpdateTimeTask);
                mStartTimer.setText(R.string.start_timer); // set button text
                timerRunning = false;
            }
        } else if (toggleOrReset == RESET_TIMER) {
            mHandler.removeCallbacks(mUpdateTimeTask);
            mCurrentTime = 180000;
            mCurrentTimer.setText(R.string.full_time);
            mStartTimer.setText(R.string.start_timer);
            timerRunning = false;
            if (mAlarmTone != null && mAlarmTone.isPlaying()) {
                mAlarmTone.stop();
            }
        }

        return START_STICKY;
    }
}
