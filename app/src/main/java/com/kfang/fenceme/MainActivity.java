package com.kfang.fenceme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    int TO_ADD = 1;
    int TO_SUBTRACT = 0;

    View.OnClickListener createOnClickListener(final TextView score, final int toAdd) {
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
                score.setText(Integer.toString(value));
            }

        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView redScore = (TextView) findViewById(R.id.red_score);
        final TextView greenScore = (TextView) findViewById(R.id.green_score);

        Button addRed = (Button) findViewById(R.id.plus_red);
        addRed.setOnClickListener(createOnClickListener(redScore, TO_ADD));
        Button subtractRed = (Button) findViewById(R.id.minus_red);
        subtractRed.setOnClickListener(createOnClickListener(redScore, TO_SUBTRACT));
        Button addGreen = (Button) findViewById(R.id.plus_green);
        addGreen.setOnClickListener(createOnClickListener(greenScore, TO_ADD));
        Button subtractGreen = (Button) findViewById(R.id.minus_green);
        subtractGreen.setOnClickListener(createOnClickListener(greenScore, TO_SUBTRACT));

        final TextView currentTimer = (TextView) findViewById(R.id.timer);
        Button startTimer = (Button) findViewById(R.id.start_timer);


    }
}
