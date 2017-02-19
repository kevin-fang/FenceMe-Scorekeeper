package com.kfang.fenceme;


import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.preference.PreferenceFragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Drawer Item CLick Listener
 */

public class DrawerItemClickListener implements ListView.OnItemClickListener {
    static String START_SETTINGS = "com.kfang.fenceme.startsettings";
    private Activity mActivity;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    public DrawerItemClickListener(Activity activity, DrawerLayout drawerLayout, ListView drawerList) {
        mActivity = activity;
        mDrawerLayout = drawerLayout;
        mDrawerList = drawerList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {
        mDrawerLayout.closeDrawer(Gravity.START);
        String selectedItem = MainActivity.mPreferenceTitles[position];
        FragmentManager fragmentManager = mActivity.getFragmentManager();

        switch (selectedItem) {
            case "Preferences":

                PreferenceFragment fragment = new SettingsActivity.MyPreferenceFragment();
                for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
                    fragmentManager.popBackStack();
                }

                fragmentManager.beginTransaction()
                        .add(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .commit();

                mDrawerList.clearChoices();
                break;
            case "Card a Player":
                mActivity.startActivity(new Intent(mActivity, CardPlayerActivity.class));
                break;
            case "Scorekeeper":
                fragmentManager.popBackStack();
                break;
            case "Tiebreaker":
                MainActivity.makeTieBreaker(mActivity);
                break;
        }
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
