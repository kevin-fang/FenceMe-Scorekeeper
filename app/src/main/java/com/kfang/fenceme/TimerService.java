package com.kfang.fenceme;

import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import static com.kfang.fenceme.MainActivity.mCurrentTime;


/**
 * Timer Service - keep the time calculations off the UI thread.
 */

public class TimerService extends Service {
    // constants used for signaling tents
    public static int TOGGLE_TIMER = 3;
    public static int RESET_TIMER = 4;
    public static int SET_TIMER = 5;
    public static String MINUTES = "minutes";
    public static String SECONDS = "seconds";
    public static String UPDATE_TIME_INTENT = "com.kfang.fenceme.updatetime";
    public static String UPDATE_TOGGLE_BUTTON_INTENT = "com.kfang.fenceme.updatetimebutton";
    public static String RESET_TIMER_INTENT = "com.kfang.fenceme.resettimer";
    public static String SET_TIMER_INTENT = "com.kfang.fenceme.settimer";
    public static String UPDATE_BUTTON_TEXT = "to_update";
    public static boolean mTimerRunning = false;
    // keep track of start times and end times
    long mStartTime = 0;

    private Ringtone mAlarmTone;
    private Handler mHandler = new Handler();

    // task to update time and fire intents when needed
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            // subtract 1000 ms every second.
            mCurrentTime = (int) (mCurrentTime - 1000);

            if (mCurrentTime > 0) {
                int newTime = (int) mCurrentTime / 1000;
                int seconds = newTime % 60;
                int minutes = newTime / 60;
                createUpdateTimeIntent(minutes, seconds);
                mHandler.postDelayed(this, 1000);
            } else if (mCurrentTime == 0) { // play an alarm if the time is up.
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                mAlarmTone = RingtoneManager.getRingtone(getApplicationContext(), notification);
                mAlarmTone.play();

                createUpdateTimeIntent(0, 0);
                /* Toast timerUp = Toast.makeText(getApplicationContext(), "Time's Up!", Toast.LENGTH_SHORT);
                timerUp.show(); */
            }
        }
    };
    @Override
    public IBinder onBind(Intent i) {
        return null;
    }

    // create an update_time_intent and fire it to the broadcastReceiver
    private void createUpdateTimeIntent(int minutes, int seconds) {
        Intent intent = new Intent(UPDATE_TIME_INTENT);
        intent.putExtra(MINUTES, minutes);
        intent.putExtra(SECONDS, seconds);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    // create an update_toggle_button_intent and fire it to the broadcastReceiver
    private void createUpdateToggleButtonIntent(int timerValue) {
        Intent intent = new Intent(UPDATE_TOGGLE_BUTTON_INTENT);
        intent.putExtra(UPDATE_BUTTON_TEXT, getResources().getString(timerValue));
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    // update time and fire intents to upddate when called.
    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
        int toggleOrReset; // value to indicate whether the button being pressed is resetting the timer or starting/pausing it.
        try {
            toggleOrReset = i.getIntExtra("TOGGLE", TOGGLE_TIMER);
        } catch (NullPointerException e) {

            toggleOrReset = RESET_TIMER; // bugfix - when starting the app, onStartCommand will run. Reset the timer when the app will start.
        }
        if (toggleOrReset == TOGGLE_TIMER) { // toggle the timer.
            if (!mTimerRunning) {
                mStartTime = System.currentTimeMillis();
                mHandler.removeCallbacks(mUpdateTimeTask);
                mHandler.postDelayed(mUpdateTimeTask, 1000);
                createUpdateToggleButtonIntent(R.string.stop_timer);
                mTimerRunning = true;
            } else {
                mHandler.removeCallbacks(mUpdateTimeTask);
                createUpdateToggleButtonIntent(R.string.start_timer);
                mTimerRunning = false;
            }
        } else if (toggleOrReset == RESET_TIMER) { // reset the timer.
            mHandler.removeCallbacks(mUpdateTimeTask);

            Intent intent = new Intent(RESET_TIMER_INTENT);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            createUpdateToggleButtonIntent(R.string.start_timer);

            mTimerRunning = false;
            if (mAlarmTone != null && mAlarmTone.isPlaying()) { // stop the alarm if it is currently playing.
                mAlarmTone.stop();
            }
        } else if (toggleOrReset == SET_TIMER) { // set timer to value
            mHandler.removeCallbacks(mUpdateTimeTask);
            Log.d("MCURERENTTIME", "In TimerService: " + mCurrentTime);
            Intent intent = new Intent(SET_TIMER_INTENT);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }

        return START_STICKY;
    }
}
