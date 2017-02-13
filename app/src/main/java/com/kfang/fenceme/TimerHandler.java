package com.kfang.fenceme;

import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;


/**
 * Timer Handler
 */

public class TimerHandler extends Service {
    // constants used for signaling tents
    public static int TOGGLE_TIMER = 3;
    public static int RESET_TIMER = 4;
    public static String MINUTES = "minutes";
    public static String SECONDS = "seconds";
    public static String UPDATE_TIME_INTENT = "com.kfang.fenceme.updatetime";
    public static String UPDATE_TOGGLE_BUTTON_INTENT = "com.kfang.fenceme.updatetimebutton";
    public static String UPDATE_BUTTON_TEXT = "to_update";
    public boolean timerRunning = false;

    // keep track of start times and end times
    long mStartTime = 0;
    private long mCurrentTime = 180000;
    private Ringtone mAlarmTone;
    private Handler mHandler = new Handler();

    // task to update time and fire intents when needed
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            mCurrentTime = (int) (mCurrentTime - 1000);

            if (mCurrentTime > 0) {
                int newTime = (int) mCurrentTime / 1000;
                int seconds = newTime % 60;
                int minutes = newTime / 60;
                createUpdateTimeIntent(minutes, seconds);
                mHandler.postDelayed(this, 1000);
            } else if (mCurrentTime == 0) {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                mAlarmTone = RingtoneManager.getRingtone(getApplicationContext(), notification);
                mAlarmTone.play();

                createUpdateTimeIntent(0, 0);
                Toast timerUp = Toast.makeText(getApplicationContext(), "Time's Up!", Toast.LENGTH_SHORT);
                timerUp.show();
            }
        }
    };
    @Override
    public IBinder onBind(Intent i) {
        return null;
    }

    // create an update_time_intent and fire it.
    private void createUpdateTimeIntent(int minutes, int seconds) {
        Intent intent = new Intent(UPDATE_TIME_INTENT);
        intent.putExtra(MINUTES, minutes);
        intent.putExtra(SECONDS, seconds);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    // create an update_toggle_button_intent and fire it.
    private void createUpdateToggleButtonIntent(int timerValue) {
        Intent intent = new Intent(UPDATE_TOGGLE_BUTTON_INTENT);
        intent.putExtra(UPDATE_BUTTON_TEXT, getResources().getString(timerValue));
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    // update time and fire intents if necessary.
    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
        int toggleOrReset = i.getIntExtra("TOGGLE", TOGGLE_TIMER);
        if (toggleOrReset == TOGGLE_TIMER) {
            if (!timerRunning) {
                mStartTime = System.currentTimeMillis();
                mHandler.removeCallbacks(mUpdateTimeTask);
                mHandler.postDelayed(mUpdateTimeTask, 1000);
                createUpdateToggleButtonIntent(R.string.stop_timer);
                timerRunning = true;
            } else {
                mHandler.removeCallbacks(mUpdateTimeTask);
                createUpdateToggleButtonIntent(R.string.start_timer);
                timerRunning = false;
            }
        } else if (toggleOrReset == RESET_TIMER) {
            mHandler.removeCallbacks(mUpdateTimeTask);
            mCurrentTime = 180000;
            createUpdateTimeIntent(3, 0);

            createUpdateToggleButtonIntent(R.string.start_timer);
            timerRunning = false;
            if (mAlarmTone != null && mAlarmTone.isPlaying()) {
                mAlarmTone.stop();
            }
        }

        return START_STICKY;
    }
}
