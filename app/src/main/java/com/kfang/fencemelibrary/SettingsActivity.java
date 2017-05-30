package com.kfang.fencemelibrary;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.kfang.fencemelibrary.main.NumberPickerPreference;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Preference Activity
 */

public class SettingsActivity extends AppCompatActivity {

    static SharedPreferences prefs;

    @BindView(R2.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pref_activity_toolbar);
        ButterKnife.bind(this);
        toolbar.setTitle(R.string.settings);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getFragmentManager().beginTransaction().replace(R.id.content_frame, new MyPreferenceFragment()).commit();
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
            final NumberPickerPreference boutMinutesPreference = (NumberPickerPreference) findPreference(Constants.BOUT_LENGTH_MINUTES);
            final NumberPickerPreference boutPointsPreference = (NumberPickerPreference) findPreference(Constants.BOUT_LENGTH_POINTS);
            final CheckBoxPreference pausePreference = (CheckBoxPreference) findPreference(Constants.PAUSE_ON_SCORE_CHANGE);
            final CheckBoxPreference awakePreference = (CheckBoxPreference) findPreference(Constants.KEEP_DEVICE_AWAKE);
            final CheckBoxPreference vibrateTimerPreference = (CheckBoxPreference) findPreference(Constants.VIBRATE_TIMER);
            final CheckBoxPreference restorePreference = (CheckBoxPreference) findPreference(Constants.RESTORE_ON_EXIT);
            final CheckBoxPreference popupPreference = (CheckBoxPreference) findPreference(Constants.POPUP_ON_SCORE);
            final CheckBoxPreference vibratePreference = (CheckBoxPreference) findPreference(Constants.VIBRATE_AT_END);
            final CheckBoxPreference doubleTouchPreference = (CheckBoxPreference) findPreference(Constants.TOGGLE_DOUBLE_TOUCH);
            final Preference resetPreferences = findPreference(Constants.RESET_BOUT_PREFERENCES);

            resetPreferences.setOnPreferenceClickListener(preference -> {
                boutMinutesPreference.setValue(Constants.DEFAULT_MINUTES);
                boutPointsPreference.setValue(Constants.DEFAULT_POINTS);
                settingsEditor.putInt(Constants.BOUT_LENGTH_POINTS, Constants.DEFAULT_POINTS);
                settingsEditor.putInt(Constants.BOUT_LENGTH_MINUTES, Constants.DEFAULT_MINUTES);

                /* Intent stopTimer = new Intent(getActivity(), TimerService.class);
                stopTimer.putExtra(Constants.CHANGE_TIMER, TimerService.RESET_TIMER);
                getActivity().startService(stopTimer); */

                return true;
            });

            SharedPreferences.OnSharedPreferenceChangeListener spChanged = (sharedPreferences, key) -> {
                switch (key) {
                    case Constants.BOUT_LENGTH_MINUTES:
                        settingsEditor.putInt(Constants.BOUT_LENGTH_MINUTES, boutMinutesPreference.getValue());
                        break;
                    case Constants.BOUT_LENGTH_POINTS:
                        settingsEditor.putInt(Constants.BOUT_LENGTH_POINTS, boutPointsPreference.getValue());
                        break;
                    case Constants.RESTORE_ON_EXIT:
                        settingsEditor.putBoolean(Constants.RESTORE_ON_EXIT, restorePreference.isChecked());
                        break;
                    case Constants.VIBRATE_AT_END:
                        settingsEditor.putBoolean(Constants.VIBRATE_AT_END, vibratePreference.isChecked());
                        break;
                    case Constants.PAUSE_ON_SCORE_CHANGE:
                        settingsEditor.putBoolean(Constants.PAUSE_ON_SCORE_CHANGE, pausePreference.isChecked());
                        break;
                    case Constants.KEEP_DEVICE_AWAKE:
                        /*if (presenter.timerRunning()) { // if timer is already running, don't let screen turn off
                            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        } */
                        settingsEditor.putBoolean(Constants.KEEP_DEVICE_AWAKE, awakePreference.isChecked());
                        break;
                    case Constants.POPUP_ON_SCORE:
                        settingsEditor.putBoolean(Constants.POPUP_ON_SCORE, popupPreference.isChecked());
                        break;
                    case Constants.VIBRATE_TIMER:
                        settingsEditor.putBoolean(Constants.VIBRATE_TIMER, vibrateTimerPreference.isChecked());
                        break;
                    case Constants.TOGGLE_DOUBLE_TOUCH:
                        settingsEditor.putBoolean(Constants.TOGGLE_DOUBLE_TOUCH, doubleTouchPreference.isChecked());
                        break;
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
