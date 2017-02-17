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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }



    public static class MyPreferenceFragment extends PreferenceFragment {
        SharedPreferences prefs;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            final NumberPickerPreference mBoutMinutesPreference = (NumberPickerPreference) findPreference(Preferences.BOUT_LENGTH_MINUTES);
            final NumberPickerPreference mBoutPointsPreference = (NumberPickerPreference) findPreference(Preferences.BOUT_LENGTH_POINTS);

                    SharedPreferences.OnSharedPreferenceChangeListener spChanged = new
                    SharedPreferences.OnSharedPreferenceChangeListener() {
                        @Override
                        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                              String key) {
                            SharedPreferences.Editor settingsEditor = prefs.edit();
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            switch (key) {
                                case Preferences.BOUT_LENGTH_MINUTES:
                                    settingsEditor.putInt(Preferences.BOUT_LENGTH_MINUTES, mBoutMinutesPreference.getValue());
                                    settingsEditor.apply();
                                    break;
                                case Preferences.BOUT_LENGTH_POINTS:
                                    settingsEditor.putInt(Preferences.BOUT_LENGTH_POINTS, mBoutPointsPreference.getValue());
                                    settingsEditor.apply();
                                    break;
                            }
                        }
                    };
            prefs.registerOnSharedPreferenceChangeListener(spChanged);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }


}
