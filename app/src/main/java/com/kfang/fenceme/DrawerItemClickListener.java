package com.kfang.fenceme;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.widget.Toast;

import static com.kfang.fenceme.MainActivity.mGreenFencer;
import static com.kfang.fenceme.MainActivity.mRedFencer;
import static com.kfang.fenceme.TimerService.RESET_BOUT_INTENT;
import static com.kfang.fenceme.TimerService.mTimerRunning;
import static com.kfang.fenceme.Utility.TO_CARD_PLAYER;

/**
 * Drawer Item CLick Listener
 */

class DrawerItemClickListener implements NavigationView.OnNavigationItemSelectedListener {
    private Activity mActivity;
    private DrawerLayout mDrawerLayout;

    DrawerItemClickListener(Activity activity, DrawerLayout drawerLayout) {
        mActivity = activity;
        mDrawerLayout = drawerLayout;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        String selectedItem = menuItem.getTitle().toString();
        //Toast.makeText(mActivity, "selected: " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
        mDrawerLayout.closeDrawers();
        FragmentManager fragmentManager = mActivity.getFragmentManager();
        fragmentManager.popBackStack();

        switch (selectedItem) {
            case "Settings":
                PreferenceFragment fragment = new SettingsActivity.MyPreferenceFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.drawer_layout, fragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case "Card a Player":
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                final String[] playerArray = {mRedFencer.getName(), mGreenFencer.getName(), "Reset Cards"};
                builder.setTitle("Card a player")
                        .setItems(playerArray, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String player = playerArray[which];
                                if (player.equals("Reset Cards")) {
                                    MainActivity.resetPlayerCards();
                                    Toast.makeText(mActivity, "Cards have been reset!", Toast.LENGTH_SHORT).show();
                                } else {
                                    // create intent to card player and pause timer
                                    if (mTimerRunning) {
                                        Intent startTimer = new Intent(mActivity, TimerService.class);
                                        startTimer.putExtra(Utility.CHANGE_TIMER, TimerService.TOGGLE_TIMER);
                                        mActivity.startService(startTimer);
                                    }
                                    Intent cardPlayer = new Intent(mActivity, CardPlayerActivity.class);
                                    cardPlayer.putExtra(TO_CARD_PLAYER, player);
                                    mActivity.startActivity(cardPlayer);
                                }
                            }
                        })
                        .create()
                        .show();
                break;
            case "Scorekeeper":
                break;
            case "Tiebreaker":
                mRedFencer.setPoints(0);
                mGreenFencer.setPoints(0);
                MainActivity.makeTieBreaker(mActivity);
                break;
            case "Reset Bout":
                AlertDialog.Builder resetBuilder = new AlertDialog.Builder(mActivity);
                resetBuilder.setTitle("Are you sure?")
                        .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                LocalBroadcastManager.getInstance(mActivity).sendBroadcast(new Intent(RESET_BOUT_INTENT));
                                Toast.makeText(mActivity, "Bout reset!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setMessage("Resetting will reset all points, the timer, and all cards awarded.")
                        .create()
                        .show();
                break;
        }
        return true;
        /*
        if (selectedItem.equals("Preferences")) {
            PreferenceFragment fragment = new SettingsActivity.MyPreferenceFragment();
            for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
                fragmentManager.popBackStack();
            }

            fragmentManager.beginTransaction()
                    .add(R.id.content_frame, fragment)
                    .addToBackStack(null)
                    .commit();

            mDrawerList.clearChoices();
            //LocalBroadcastManager.getInstance(mActivity).sendBroadcast(new Intent(START_SETTINGS));
        } else if (selectedItem.equals("Card a Player")) {
            mActivity.startActivity(new Intent(mActivity, CardPlayerActivity.class));
        } else if (selectedItem.equals("Scorekeeper")) {
           fragmentManager.popBackStack();
        } else if (selectedItem.equals("Tiebreaker")) {
            MainActivity.makeTieBreaker(mActivity);
        }
        */
    }

}
