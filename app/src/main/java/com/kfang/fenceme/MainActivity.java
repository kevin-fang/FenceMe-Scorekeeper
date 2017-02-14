package com.kfang.fenceme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    public static final int TO_ADD = 1;
    public static final int TO_SUBTRACT = 0;
    public static long mCurrentTime = 180000;
    Button mStartTimer;
    TextView mCurrentTimer;
    // buttons in main drawable resource file
    Button addRed;
    Button subtractRed;
    Button addGreen;
    Button subtractGreen;
    Button resetTimer;
    TextView greenScore;
    TextView redScore;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mCurrentTimePermanent = Preferences.updateCurrentTime(this) * 60000;
        // set textviews and buttons for scorekeeping
        redScore = (TextView) findViewById(R.id.red_score);
        greenScore = (TextView) findViewById(R.id.green_score);
        addRed = (Button) findViewById(R.id.plus_red);
        subtractRed = (Button) findViewById(R.id.minus_red);
        addGreen = (Button) findViewById(R.id.plus_green);
        subtractGreen = (Button) findViewById(R.id.minus_green);

        // set onclickListeners for buttons
        addRed.setOnClickListener(createOnClickListener(redScore, TO_ADD));
        subtractRed.setOnClickListener(createOnClickListener(redScore, TO_SUBTRACT));
        addGreen.setOnClickListener(createOnClickListener(greenScore, TO_ADD));
        subtractGreen.setOnClickListener(createOnClickListener(greenScore, TO_SUBTRACT));

        // set textviews and buttons for timekeeping
        mStartTimer = (Button) findViewById(R.id.start_timer);
        resetTimer = (Button) findViewById(R.id.reset_timer);
        mCurrentTimer = (TextView) findViewById(R.id.timer);

        int seconds = (int) (mCurrentTime / 1000) % 60;
        Toast.makeText(this, "seconds: " + mCurrentTime, Toast.LENGTH_SHORT).show();
        if (seconds < 10) {
            mCurrentTimer.setText("" + mCurrentTime / 1000 / 60 + ":0" + seconds);
        } else {
            mCurrentTimer.setText("" + mCurrentTime / 1000 / 60 + ":" + seconds);
        }

        // set onClickListener for start and reset
        mStartTimer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startTimer = new Intent(getApplicationContext(), TimerService.class);
                startTimer.putExtra("TOGGLE", TimerService.TOGGLE_TIMER);
                startService(startTimer);
            }
        });
        resetTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stopTimer = new Intent(getApplicationContext(), TimerService.class);
                stopTimer.putExtra("TOGGLE", TimerService.RESET_TIMER);
                startService(stopTimer);
            }
        });

        // LocalBroadcastManagers to deal with updating time and toggle button text intents.
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // set the text in the textview to corresponding minutes and seconds
                        int minutes = intent.getIntExtra(TimerService.MINUTES, 0);
                        int seconds = intent.getIntExtra(TimerService.SECONDS, 0);
                        if (seconds < 10 && seconds >= 0) {
                            mCurrentTimer.setText("" + minutes + ":0" + seconds);
                        } else {
                            mCurrentTimer.setText("" + minutes + ":" + seconds);
                        }
                    }
                }, new IntentFilter(TimerService.UPDATE_TIME_INTENT)
        );

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // set text in button to corresponding value.
                        String text = intent.getStringExtra(TimerService.UPDATE_BUTTON_TEXT);
                        mStartTimer.setText(text);
                    }
                }, new IntentFilter(TimerService.UPDATE_TOGGLE_BUTTON_INTENT)
        );

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // set the text in the textview to corresponding minutes and seconds
                        int minutes = Preferences.updateCurrentTime(getApplicationContext());
                        mCurrentTime = minutes * 60000;
                        mCurrentTimer.setText("" + minutes + ":00");
                    }
                }, new IntentFilter(TimerService.RESET_TIMER_INTENT)
        );
    }


    // reset scores
    public void resetScores(View v) {
        redScore.setText(String.format("%s", 0));
        greenScore.setText(String.format("%s", 0));
    }

    // create options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    // create activites for options menu selections
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.card:
                startActivity(new Intent(this, CardPlayerActivity.class));
                return true;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // create an on click listener for each of the plus/minus buttons.
    private View.OnClickListener createOnClickListener(final TextView score, final int toAdd) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valueStr = score.getText().toString();
                int value = Integer.parseInt(valueStr);
                if (toAdd == TO_ADD && value < Preferences.getIntPreference(getApplicationContext(), Preferences.BOUT_LENGTH_POINTS, Preferences.DEFAULT_POINTS)) {
                    value += 1;
                } else if (toAdd == TO_SUBTRACT && value > 0) {
                    value -= 1;
                }
                score.setText(String.format("%s", value));
            }

        };
    }
}
