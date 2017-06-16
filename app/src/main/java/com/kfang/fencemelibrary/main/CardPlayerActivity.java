package com.kfang.fencemelibrary.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kfang.fencemelibrary.R;
import com.kfang.fencemelibrary.R2;
import com.kfang.fencemelibrary.model.Fencer;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.kfang.fencemelibrary.main.MainActivity.LOG_TAG;


public class CardPlayerActivity extends AppCompatActivity {

    public static final String RED_FENCER = "red.fencer";
    public static final String GREEN_FENCER = "green.fencer";
    public static final String FENCER_TO_CARD = "fencer.to.card";
    public static final String RETURN_CARD = "return.card";
    public static final String YELLOW_CARD = "yellow.card";
    public static final String RED_CARD = "red.card";

    @BindView(R2.id.yellow_card)
    Button yellowButton;
    @BindView(R2.id.red_card)
    Button redButton;
    @BindView(R2.id.currently_carding)
    TextView currentlyCarding;

    LinearLayout cardLayout;

    Fencer redFencer;
    Fencer greenFencer;
    String cardingFencerName;
    Fencer cardingFencer;

    // TODO: Change card player to fragment
    // TODO: Full brightness + setting
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        Intent intent = getIntent();

        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.nav_card_player);
        }
        Bundle b = intent.getExtras();
        redFencer = (Fencer) b.getSerializable(RED_FENCER);
        greenFencer = (Fencer) b.getSerializable(GREEN_FENCER);
        cardingFencerName = b.getString(FENCER_TO_CARD);
        //currentlyCarding = (TextView) findViewById(R.id.currently_carding);
        if (cardingFencerName.equals(redFencer.getName())) {
            cardingFencer = redFencer;
        } else {
            cardingFencer = greenFencer;
        }
        currentlyCarding.setText(String.format(getString(R.string.currently_carding), cardingFencer.getName()));

        yellowButton.setText(String.format(Locale.getDefault(), "Yellow\nCard\n%d", cardingFencer.getYellowCards()));
        redButton.setText(String.format(Locale.getDefault(), "Red\nCard\n%d", cardingFencer.getRedCards()));
        cardLayout = (LinearLayout) findViewById(R.id.card_layout);
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

    @OnClick(R2.id.red_card)
    // display different cards based on what was clicked
    public void displayRed(View v) {
        // Toast.makeText(this, "Clicked Red", Toast.LENGTH_SHORT).show();
        Fencer oppositeFencer;
        if (cardingFencer == redFencer) {
            Log.d(LOG_TAG, "Incremented red for: " + redFencer.getName());
            oppositeFencer = greenFencer;
        } else {
            Log.d(LOG_TAG, "Incremented red for: " + greenFencer.getName());
            oppositeFencer = redFencer;
        }

        Toast.makeText(getApplicationContext(), "Gave point to " + oppositeFencer.getName(), Toast.LENGTH_SHORT).show();
        setContentView(R.layout.card_display);
        View cardView = findViewById(R.id.card);
        cardView.setBackground(ContextCompat.getDrawable(this, R.drawable.redcard));
        Intent returnIntent = new Intent();
        returnIntent.putExtra(FENCER_TO_CARD, cardingFencerName);
        returnIntent.putExtra(RETURN_CARD, RED_CARD);
        setResult(Activity.RESULT_OK, returnIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R2.id.yellow_card)
    public void displayYellow(View v) {
        setContentView(R.layout.card_display);
        View cardView = findViewById(R.id.card);
        cardView.setBackground(ContextCompat.getDrawable(this, R.drawable.yellowcard));
        Intent returnIntent = new Intent();
        returnIntent.putExtra(FENCER_TO_CARD, cardingFencerName);
        returnIntent.putExtra(RETURN_CARD, YELLOW_CARD);
        setResult(Activity.RESULT_OK, returnIntent);
    }

    @OnClick(R2.id.black_card)
    public void displayBlack(View v) {
        setContentView(R.layout.card_display);
        View cardView = findViewById(R.id.card);
        cardView.setBackground(ContextCompat.getDrawable(this, R.drawable.blackcard));
    }

    public void dismissCard(View v) {
        finish();
    }

}
