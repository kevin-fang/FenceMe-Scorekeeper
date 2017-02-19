package com.kfang.fenceme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * Preference Activity
 */

public class SettingsActivity extends AppCompatActivity {

    static SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            //final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            final NumberPickerPreference mBoutMinutesPreference = (NumberPickerPreference) findPreference(Utility.BOUT_LENGTH_MINUTES);
            final NumberPickerPreference mBoutPointsPreference = (NumberPickerPreference) findPreference(Utility.BOUT_LENGTH_POINTS);
            final SharedPreferences.Editor settingsEditor = prefs.edit();

            SharedPreferences.OnSharedPreferenceChangeListener spChanged = new
                    SharedPreferences.OnSharedPreferenceChangeListener() {
                        @Override
                        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                              String key) {
                            switch (key) {
                                case Utility.BOUT_LENGTH_MINUTES:
                                    settingsEditor.putInt(Utility.BOUT_LENGTH_MINUTES, mBoutMinutesPreference.getValue());
                                    break;
                                case Utility.BOUT_LENGTH_POINTS:
                                    settingsEditor.putInt(Utility.BOUT_LENGTH_POINTS, mBoutPointsPreference.getValue());
                                    break;
                            }

                        }
                    };
            prefs.registerOnSharedPreferenceChangeListener(spChanged);
            settingsEditor.apply();
        }
    }


}
