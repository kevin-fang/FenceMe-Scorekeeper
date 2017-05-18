package com.kfang.fencemelibrary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.kfang.fencemelibrary.MainActivity.mGreenFencer;
import static com.kfang.fencemelibrary.MainActivity.mRedFencer;


public class CardPlayerActivity extends AppCompatActivity {
    String playerToCard;
    @BindView(R2.id.yellow_card)
    Button yellowButton;
    @BindView(R2.id.red_card)
    Button redButton;
    @BindView(R2.id.black_card)
    Button blackButton;
    int numYellow = 0;
    int numRed = 0;
    @BindView(R2.id.currently_carding)
    TextView currentlyCarding;
    LinearLayout cardLayout;



    // TODO: Change card player to fragment
    // TODO: Full brightness + setting
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        Intent intent = getIntent();

        ButterKnife.bind(this);

        playerToCard = intent.getStringExtra(Utility.TO_CARD_PLAYER);
        if (playerToCard.equals(mRedFencer.getName())) {
            numYellow = mRedFencer.getYellowCards();
            numRed = mRedFencer.getRedCards();
            currentlyCarding.setText(getString(R.string.currently_carding) + " " + mRedFencer.getName());
        } else if (playerToCard.equals(MainActivity.mGreenFencer.getName())) {
            numYellow = mGreenFencer.getYellowCards();
            numRed = mGreenFencer.getRedCards();
            currentlyCarding.setText(getString(R.string.currently_carding) + " " + mGreenFencer.getName());
        }
        yellowButton.setText(String.format(Locale.getDefault(), "Yellow\nCard\n%d", numYellow));
        redButton.setText(String.format(Locale.getDefault(), "Red\nCard\n%d", numRed));
        cardLayout = (LinearLayout) findViewById(R.id.card_layout);
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
        Fencer oppositeFencer;
        if (playerToCard.equals(mRedFencer.getName())) {
            oppositeFencer = mGreenFencer;
            mRedFencer.incrementRedCards();
        } else {
            oppositeFencer = mRedFencer;
            mGreenFencer.incrementRedCards();
        }
        oppositeFencer.incrementNumPoints();

        Toast.makeText(getApplicationContext(), "Gave point to " + oppositeFencer.getName(), Toast.LENGTH_SHORT).show();
        setContentView(R.layout.card_display);
        View cardView = findViewById(R.id.card);
        cardView.setBackground(ContextCompat.getDrawable(this, R.drawable.redcard));
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void displayYellow(View v) {
        if (playerToCard.equals(mRedFencer.getName())) {
            mRedFencer.incrementYellowCards();
        } else if (playerToCard.equals(mGreenFencer.getName())) {
            mGreenFencer.incrementYellowCards();
        }
        setContentView(R.layout.card_display);
        View cardView = findViewById(R.id.card);
        cardView.setBackground(ContextCompat.getDrawable(this, R.drawable.yellowcard));
    }

    public void displayBlack(View v) {
        setContentView(R.layout.card_display);
        View cardView = findViewById(R.id.card);
        cardView.setBackground(ContextCompat.getDrawable(this, R.drawable.blackcard));
    }

    public void dismissCard(View v) {
        finish();
    }

}
