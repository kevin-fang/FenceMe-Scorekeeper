package com.kfang.fenceme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView redScore = (TextView) findViewById(R.id.red_score);
        TextView greenScore = (TextView) findViewById(R.id.green_score);

        Button addRed = (Button) findViewById(R.id.plus_red);
        addRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String redValueStr = redScore.getText().toString();
                int redValue = Integer.parseInt(redValueStr);
                redValue += 1;
                redScore.setText(Integer.toString(redValue));
                /* Context context = getApplicationContext();
                Toast toast = Toast.makeText(context, redValue, Toast.LENGTH_SHORT);
                toast.show(); */
            }
        });
        Button subtractRed = (Button) findViewById(R.id.minus_red);
        Button addGreen = (Button) findViewById(R.id.plus_green);
        Button subtractGreen = (Button) findViewById(R.id.minus_green);


    }
}
