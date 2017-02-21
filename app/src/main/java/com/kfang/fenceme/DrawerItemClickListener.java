package com.kfang.fenceme;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import static com.kfang.fenceme.Utility.TO_CARD_PLAYER;
import static com.kfang.fenceme.Utility.greenName;
import static com.kfang.fenceme.Utility.redName;

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

    /**
     * Swaps fragments in the main content view
     */
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
                        .add(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case "Card a Player":
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                final String[] playerArray = {redName, greenName, "Reset Cards"};
                builder.setTitle("Card a player")
                        .setItems(playerArray, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String player = playerArray[which];
                                if (player.equals("Reset Cards")) {
                                    MainActivity.resetPlayerCards();
                                    Toast.makeText(mActivity, "Cards have been reset!", Toast.LENGTH_SHORT).show();
                                } else {
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
                MainActivity.makeTieBreaker(mActivity);
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
