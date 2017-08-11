package com.kfang.fencemelibrary.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.kfang.fencemelibrary.R
import com.kfang.fencemelibrary.model.Fencer
import kotlinx.android.synthetic.main.activity_card.*
import java.util.*

class CardPlayerActivity : AppCompatActivity() {

    lateinit var redFencer: Fencer
    lateinit var greenFencer: Fencer
    lateinit var cardingFencerName: String
    lateinit var cardingFencer: Fencer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)
        val intent = intent

        supportActionBar?.setTitle(R.string.nav_card_player)

        val b = intent.extras
        redFencer = b.getSerializable(RED_FENCER) as Fencer
        greenFencer = b.getSerializable(GREEN_FENCER) as Fencer
        cardingFencerName = b.getString(FENCER_TO_CARD)
        //currentlyCarding = (TextView) findViewById(R.id.currently_carding);
        if (cardingFencerName == redFencer.name) {
            cardingFencer = redFencer
        } else {
            cardingFencer = greenFencer
        }
        currently_carding.text = String.format(getString(R.string.currently_carding), cardingFencer.name)

        yellow_card.text = String.format(Locale.getDefault(), "Yellow\nCard\n%d", cardingFencer.yellowCards)
        yellow_card.setOnClickListener { displayYellow() }
        red_card.text = String.format(Locale.getDefault(), "Red\nCard\n%d", cardingFencer.redCards)
        red_card.setOnClickListener { displayRed() }

        black_card.setOnClickListener { displayBlack() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // app icon in action bar clicked; goto parent activity.
                this.finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun displayRed() {
        // Toast.makeText(this, "Clicked Red", Toast.LENGTH_SHORT).show();
        val oppositeFencer: Fencer
        if (cardingFencer === redFencer) {
            Log.d(MainActivity.LOG_TAG, "Incremented red for: " + redFencer.name)
            oppositeFencer = greenFencer
        } else {
            Log.d(MainActivity.LOG_TAG, "Incremented red for: " + greenFencer.name)
            oppositeFencer = redFencer
        }

        Toast.makeText(applicationContext, "Gave point to " + oppositeFencer.name, Toast.LENGTH_SHORT).show()
        setContentView(R.layout.card_display)
        val cardView = findViewById(R.id.card)
        cardView.background = ContextCompat.getDrawable(this, R.drawable.redcard)
        val returnIntent = Intent()
        returnIntent.putExtra(FENCER_TO_CARD, cardingFencerName)
        returnIntent.putExtra(RETURN_CARD, RED_CARD)
        setResult(Activity.RESULT_OK, returnIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun displayYellow() {
        setContentView(R.layout.card_display)
        val cardView = findViewById(R.id.card)
        cardView.background = ContextCompat.getDrawable(this, R.drawable.yellowcard)
        val returnIntent = Intent()
        returnIntent.putExtra(FENCER_TO_CARD, cardingFencerName)
        returnIntent.putExtra(RETURN_CARD, YELLOW_CARD)
        setResult(Activity.RESULT_OK, returnIntent)
    }

    fun displayBlack() {
        setContentView(R.layout.card_display)
        val cardView = findViewById(R.id.card)
        cardView.background = ContextCompat.getDrawable(this, R.drawable.blackcard)
    }

    fun dismissCard(v: View) {
        finish()
    }

    companion object {
        val RED_FENCER = "red.fencer"
        val GREEN_FENCER = "green.fencer"
        val FENCER_TO_CARD = "fencer.to.card"
        val RETURN_CARD = "return.card"
        val YELLOW_CARD = "yellow.card"
        val RED_CARD = "red.card"
    }

}