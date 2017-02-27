package com.kfang.fenceme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;

import static com.kfang.fenceme.TimerService.mTimerRunning;

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
            prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            final SharedPreferences.Editor settingsEditor = prefs.edit();
            //final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            final NumberPickerPreference boutMinutesPreference = (NumberPickerPreference) findPreference(Utility.BOUT_LENGTH_MINUTES);
            final NumberPickerPreference boutPointsPreference = (NumberPickerPreference) findPreference(Utility.BOUT_LENGTH_POINTS);
            final CheckBoxPreference pausePreference = (CheckBoxPreference) findPreference(Utility.PAUSE_ON_SCORE_CHANGE);
            final CheckBoxPreference awakePreference = (CheckBoxPreference) findPreference(Utility.KEEP_DEVICE_AWAKE);
            final CheckBoxPreference vibrateTimerPreference = (CheckBoxPreference) findPreference(Utility.VIBRATE_TIMER);
            final CheckBoxPreference restorePreference = (CheckBoxPreference) findPreference(Utility.RESTORE_ON_EXIT);
            final CheckBoxPreference popupPreference = (CheckBoxPreference) findPreference(Utility.POPUP_ON_SCORE);
            final CheckBoxPreference vibratePreference = (CheckBoxPreference) findPreference(Utility.VIBRATE_AT_END);
            final Preference resetPreferences = findPreference(Utility.RESET_BOUT_PREFERENCES);

            resetPreferences.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    boutMinutesPreference.setValue(Utility.DEFAULT_MINUTES);
                    boutPointsPreference.setValue(Utility.DEFAULT_POINTS);
                    settingsEditor.putInt(Utility.BOUT_LENGTH_POINTS, Utility.DEFAULT_POINTS);
                    settingsEditor.putInt(Utility.BOUT_LENGTH_MINUTES, Utility.DEFAULT_MINUTES);

                    Intent stopTimer = new Intent(getActivity(), TimerService.class);
                    stopTimer.putExtra(Utility.CHANGE_TIMER, TimerService.RESET_TIMER);
                    getActivity().startService(stopTimer);

                    ((MainActivity) getActivity()).resetScores(null);
                    return true;
                }
            });

            SharedPreferences.OnSharedPreferenceChangeListener spChanged = new
                    SharedPreferences.OnSharedPreferenceChangeListener() {
                        @Override
                        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                              String key) {
                            switch (key) {
                                case Utility.BOUT_LENGTH_MINUTES:
                                    settingsEditor.putInt(Utility.BOUT_LENGTH_MINUTES, boutMinutesPreference.getValue());
                                    break;
                                case Utility.BOUT_LENGTH_POINTS:
                                    settingsEditor.putInt(Utility.BOUT_LENGTH_POINTS, boutPointsPreference.getValue());
                                    break;
                                case Utility.RESTORE_ON_EXIT:
                                    settingsEditor.putBoolean(Utility.RESTORE_ON_EXIT, restorePreference.isChecked());
                                    break;
                                case Utility.VIBRATE_AT_END:
                                    settingsEditor.putBoolean(Utility.VIBRATE_AT_END, vibratePreference.isChecked());
                                    break;
                                case Utility.PAUSE_ON_SCORE_CHANGE:
                                    settingsEditor.putBoolean(Utility.PAUSE_ON_SCORE_CHANGE, pausePreference.isChecked());
                                    break;
                                case Utility.KEEP_DEVICE_AWAKE:
                                    if (mTimerRunning) { // if timer is already running, don't let screen turn off
                                        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                    }
                                    settingsEditor.putBoolean(Utility.KEEP_DEVICE_AWAKE, awakePreference.isChecked());
                                    break;
                                case Utility.POPUP_ON_SCORE:
                                    settingsEditor.putBoolean(Utility.POPUP_ON_SCORE, popupPreference.isChecked());
                                    break;
                                case Utility.VIBRATE_TIMER:
                                    settingsEditor.putBoolean(Utility.VIBRATE_TIMER, vibrateTimerPreference.isChecked());
                                    break;
                            }

                        }
                    };
            prefs.registerOnSharedPreferenceChangeListener(spChanged);
            settingsEditor.apply();
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            if (getView() != null) {
                getView().setBackgroundColor(Color.WHITE);
            }
            getView().setClickable(true);
        }

    }

}
