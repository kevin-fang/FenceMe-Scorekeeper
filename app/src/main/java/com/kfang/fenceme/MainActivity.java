package com.kfang.fenceme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final int TO_ADD = 1;
    public static final int TO_SUBTRACT = 0;
    public static Button mStartTimer;
    public static TextView mCurrentTimer;
    // buttons in main drawable resource file
    Button addRed;
    Button subtractRed;
    Button addGreen;
    Button subtractGreen;
    Button resetTimer;


    // keeps track of the current timer value

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
                Intent startTimer = new Intent(getApplicationContext(), TimerHandler.class);
                startTimer.putExtra("TOGGLE", TimerHandler.TOGGLE_TIMER);
                startService(startTimer);
            }
        };

        mStartTimer = (Button) findViewById(R.id.start_timer);
        mStartTimer.setOnClickListener(mStartListener);

        resetTimer = (Button) findViewById(R.id.reset_timer);
        resetTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stopTimer = new Intent(getApplicationContext(), TimerHandler.class);
                stopTimer.putExtra("TOGGLE", TimerHandler.RESET_TIMER);
                startService(stopTimer);
            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                startActivity(new Intent(this, About.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
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
