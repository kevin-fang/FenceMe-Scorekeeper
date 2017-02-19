package com.kfang.fenceme;


import android.app.Activity;
import android.app.FragmentManager;
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
                mActivity.startActivity(new Intent(mActivity, CardPlayerActivity.class));
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
