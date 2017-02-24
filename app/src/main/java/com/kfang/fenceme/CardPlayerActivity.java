package com.kfang.fenceme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import static com.kfang.fenceme.Utility.GREEN_CARDRED;
import static com.kfang.fenceme.Utility.GREEN_CARDYELLOW;
import static com.kfang.fenceme.Utility.RED_CARDRED;
import static com.kfang.fenceme.Utility.RED_CARDYELLOW;
import static com.kfang.fenceme.Utility.TO_CARD_PLAYER;
import static com.kfang.fenceme.Utility.greenName;
import static com.kfang.fenceme.Utility.redName;


public class CardPlayerActivity extends AppCompatActivity {
    String playerToCard;
    Button yellowButton;
    Button redButton;
    Button blackButton;
    int numYellow = 0;
    int numRed = 0;
    TextView currentlyCarding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        Intent intent = getIntent();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        playerToCard = intent.getStringExtra(TO_CARD_PLAYER);
        yellowButton = (Button) findViewById(R.id.yellow_card);
        redButton = (Button) findViewById(R.id.red_card);
        blackButton = (Button) findViewById(R.id.black_card);
        currentlyCarding = (TextView) findViewById(R.id.currently_carding);
        if (playerToCard.equals(redName)) {
            numYellow = MainActivity.redPlayerCards.get(RED_CARDYELLOW);
            numRed = MainActivity.redPlayerCards.get(Utility.RED_CARDRED);
            currentlyCarding.setText(getString(R.string.currently_carding) + " " + redName);
        } else if (playerToCard.equals(greenName)) {
            numYellow = MainActivity.greenPlayerCards.get(Utility.GREEN_CARDYELLOW);
            numRed = MainActivity.greenPlayerCards.get(GREEN_CARDRED);
            currentlyCarding.setText(getString(R.string.currently_carding) + " " + greenName);
        }
        yellowButton.setText(String.format(Locale.getDefault(), "Yellow Card\n%d", numYellow));
        redButton.setText(String.format(Locale.getDefault(), "Red Card\n%d", numRed));
        //blackButton.setText(String.format(Locale.getDefault(), "Black Card\n%d", 0));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // display different cards based on what was clicked
    public void displayRed(View v) {
        // Toast.makeText(this, "Clicked Red", Toast.LENGTH_SHORT).show();
        if (playerToCard.equals(redName)) {
            MainActivity.redPlayerCards.put(RED_CARDRED, MainActivity.redPlayerCards.get(RED_CARDRED) + 1);
        } else if (playerToCard.equals(greenName)) {
            MainActivity.greenPlayerCards.put(GREEN_CARDRED, MainActivity.greenPlayerCards.get(GREEN_CARDRED) + 1);
        }
        setContentView(R.layout.card_display);
        View cardView = findViewById(R.id.card);
        cardView.setBackground(ContextCompat.getDrawable(this, R.drawable.redcard));
    }

    public void displayYellow(View v) {
        if (playerToCard.equals(redName)) {
            MainActivity.redPlayerCards.put(RED_CARDYELLOW, MainActivity.redPlayerCards.get(RED_CARDYELLOW) + 1);
        } else if (playerToCard.equals(greenName)) {
            MainActivity.greenPlayerCards.put(GREEN_CARDYELLOW, MainActivity.greenPlayerCards.get(GREEN_CARDYELLOW) + 1);
        }
        //Toast.makeText(this, "Clicked Yellow", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.card_display);
        View cardView = findViewById(R.id.card);
        cardView.setBackground(ContextCompat.getDrawable(this, R.drawable.yellowcard));
    }

    public void displayBlack(View v) {
        //Toast.makeText(this, "Clicked Black", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.card_display);
        View cardView = findViewById(R.id.card);
        cardView.setBackground(ContextCompat.getDrawable(this, R.drawable.blackcard));
    }

    public void dismissCard(View v) {
        finish();
    }

}
