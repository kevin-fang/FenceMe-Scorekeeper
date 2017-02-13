package com.kfang.fenceme;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;


public class CardPlayer extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
    }

    // display different cards based on what was clicked
    public void displayRed(View v) {
        // Toast.makeText(this, "Clicked Red", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.card_display);
        View cardView = findViewById(R.id.card);
        cardView.setBackground(ContextCompat.getDrawable(this, R.drawable.redcard));
    }

    public void displayYellow(View v) {
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
