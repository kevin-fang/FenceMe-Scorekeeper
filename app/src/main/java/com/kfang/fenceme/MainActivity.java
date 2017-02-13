package com.kfang.fenceme;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final int TO_ADD = 1;
    public static final int TO_SUBTRACT = 0;

    Button addRed;
    Button subtractRed;
    Button addGreen;
    Button subtractGreen;
    Button startTimer;
    Button resetTimer;

    long mStartTime = 0;
    private long mCurrentTime = 180000;
    private boolean timerRunning = false;

    private TextView mCurrentTimer;
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
            } else {
                Toast timerUp = Toast.makeText(getApplicationContext(), "Timer's Up!", Toast.LENGTH_SHORT);
                timerUp.show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView redScore = (TextView) findViewById(R.id.red_score);
        final TextView greenScore = (TextView) findViewById(R.id.green_score);
        addRed = (Button) findViewById(R.id.plus_red);
        subtractRed = (Button) findViewById(R.id.minus_red);
        addGreen = (Button) findViewById(R.id.plus_green);
        subtractGreen = (Button) findViewById(R.id.minus_green);

        addRed.setOnClickListener(createOnClickListener(redScore, TO_ADD));
        subtractRed.setOnClickListener(createOnClickListener(redScore, TO_SUBTRACT));
        addGreen.setOnClickListener(createOnClickListener(greenScore, TO_ADD));
        subtractGreen.setOnClickListener(createOnClickListener(greenScore, TO_SUBTRACT));
        mCurrentTimer = (TextView) findViewById(R.id.timer);

        final View.OnClickListener mStartListener = new View.OnClickListener() {
            public void onClick(View v) {
                if (!timerRunning) {
                    mStartTime = System.currentTimeMillis();
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    mHandler.postDelayed(mUpdateTimeTask, 1000);
                    startTimer.setText(R.string.stop_timer);
                    timerRunning = true;
                } else {
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    startTimer.setText(R.string.start_timer);
                    timerRunning = false;
                }
            }
        };
        startTimer = (Button) findViewById(R.id.start_timer);
        startTimer.setOnClickListener(mStartListener);

        resetTimer = (Button) findViewById(R.id.reset_timer);
        resetTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentTime = 180000;
                mCurrentTimer.setText(R.string.no_time);
                mHandler.removeCallbacks(mUpdateTimeTask);
                timerRunning = false;
            }
        });

    }

    private View.OnClickListener createOnClickListener(final TextView score, final int toAdd) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valueStr = score.getText().toString();
                int value = Integer.parseInt(valueStr);
                if (toAdd == TO_ADD && value < 5) {
                    value += 1;
                } else if (toAdd == TO_SUBTRACT && value > 0) {
                    value -= 1;
                }
                score.setText(String.format("%s", value));
            }

        };
    }
}
