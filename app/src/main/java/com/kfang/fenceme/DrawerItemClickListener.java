package com.kfang.fenceme;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.kfang.fenceme.NavMenu.DrawerAdapter;
import com.yarolegovich.slidingrootnav.SlidingRootNav;

import static com.kfang.fenceme.MainActivity.mGreenFencer;
import static com.kfang.fenceme.MainActivity.mRedFencer;
import static com.kfang.fenceme.TimerService.RESET_BOUT_INTENT;
import static com.kfang.fenceme.TimerService.mTimerRunning;
import static com.kfang.fenceme.Utility.TO_CARD_PLAYER;

/**
 * Drawer Item CLick Listener
 */

class DrawerItemClickListener implements DrawerAdapter.OnItemSelectedListener {
    static final int OPEN_CARD_ACTIVITY = 1234;
    private Activity activity;
    private SlidingRootNav navigationMenu;

    DrawerItemClickListener(Activity activity, SlidingRootNav navMenu) {
        this.activity = activity;
        navigationMenu = navMenu;
    }

    @Override
    public void onItemSelected(int position) {
        //Toast.makeText(activity, "selected: " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();

        switch (position) {
            case MainActivity.CARD_A_PLAYER:
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                final String[] playerArray = {mRedFencer.getName(), mGreenFencer.getName(), "Reset Cards"};
                builder.setTitle("Card a player")
                        .setItems(playerArray, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String player = playerArray[which];
                                if (player.equals("Reset Cards")) {
                                    MainActivity.resetPlayerCards();
                                    Toast.makeText(activity, "Cards have been reset!", Toast.LENGTH_SHORT).show();
                                } else {
                                    // create intent to card player and pause timer
                                    if (mTimerRunning) {
                                        Intent startTimer = new Intent(activity, TimerService.class);
                                        startTimer.putExtra(Utility.CHANGE_TIMER, TimerService.TOGGLE_TIMER);
                                        activity.startService(startTimer);
                                    }
                                    Intent cardPlayer = new Intent(activity, CardPlayerActivity.class);
                                    cardPlayer.putExtra(TO_CARD_PLAYER, player);
                                    activity.startActivityForResult(cardPlayer, OPEN_CARD_ACTIVITY);
                                }
                            }
                        })
                        .create()
                        .show();
                navigationMenu.closeMenu(true);
                break;
            case MainActivity.TIEBREAKER:
                mRedFencer.setPoints(0);
                mGreenFencer.setPoints(0);
                MainActivity.makeTieBreaker(activity);
                navigationMenu.closeMenu(true);
                break;
            case MainActivity.RESET_BOUT:
                navigationMenu.closeMenu(true);
                LocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent(RESET_BOUT_INTENT));
                break;
        }
    }

}
