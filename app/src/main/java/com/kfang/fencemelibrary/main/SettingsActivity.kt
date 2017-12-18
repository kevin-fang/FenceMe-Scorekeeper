package com.kfang.fencemelibrary.main

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.kfang.fencemelibrary.R
import com.kfang.fencemelibrary.misc.Constants
import com.kfang.fencemelibrary.misc.NumberPickerPreference


/**
 * Preference Activity
 */

class SettingsActivity : AppCompatActivity() {
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.pref_activity_toolbar)

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
        fragmentManager.beginTransaction().replace(R.id.content_frame, MyPreferenceFragment()).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId == android.R.id.home) {
            finish() // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item)
    }

    class MyPreferenceFragment : PreferenceFragment() {
        private lateinit var prefs: SharedPreferences

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            addPreferencesFromResource(R.xml.preferences)
            prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            val settingsEditor = prefs.edit()

            // set up preferences
            val boutMinutesPreference = findPreference(Constants.BOUT_LENGTH_MINUTES) as NumberPickerPreference
            boutMinutesPreference.setDefaultValue(3)

            val boutPointsPreference = findPreference(Constants.BOUT_LENGTH_POINTS) as NumberPickerPreference
            boutPointsPreference.setDefaultValue(5)

            val pausePreference = findPreference(Constants.PAUSE_ON_SCORE_CHANGE) as CheckBoxPreference
            val awakePreference = findPreference(Constants.KEEP_DEVICE_AWAKE) as CheckBoxPreference
            val vibrateTimerPreference = findPreference(Constants.VIBRATE_TIMER) as CheckBoxPreference
            val restorePreference = findPreference(Constants.RESTORE_ON_EXIT) as CheckBoxPreference
            val popupPreference = findPreference(Constants.POPUP_ON_SCORE) as CheckBoxPreference
            val vibratePreference = findPreference(Constants.VIBRATE_AT_END) as CheckBoxPreference
            val doubleTouchPreference = findPreference(Constants.TOGGLE_DOUBLE_TOUCH) as CheckBoxPreference
            val volumeButtonTogglePreference = findPreference(Constants.VOLUME_BUTTON_TIMER_TOGGLE) as CheckBoxPreference
            val resetPreferences = findPreference(Constants.RESET_BOUT_PREFERENCES)

            resetPreferences.setOnPreferenceClickListener { _ ->
                boutMinutesPreference.defaultValue = Constants.DEFAULT_MINUTES
                boutPointsPreference.defaultValue = Constants.DEFAULT_POINTS
                settingsEditor.putInt(Constants.BOUT_LENGTH_POINTS, Constants.DEFAULT_POINTS)
                settingsEditor.putInt(Constants.BOUT_LENGTH_MINUTES, Constants.DEFAULT_MINUTES)

                true
            }
            prefs.registerOnSharedPreferenceChangeListener { _, key ->
                when (key) {
                    Constants.BOUT_LENGTH_MINUTES -> settingsEditor.putInt(Constants.BOUT_LENGTH_MINUTES, boutMinutesPreference.defaultValue)
                    Constants.BOUT_LENGTH_POINTS -> settingsEditor.putInt(Constants.BOUT_LENGTH_POINTS, boutPointsPreference.defaultValue)
                    Constants.RESTORE_ON_EXIT -> settingsEditor.putBoolean(Constants.RESTORE_ON_EXIT, restorePreference.isChecked)
                    Constants.VIBRATE_AT_END -> settingsEditor.putBoolean(Constants.VIBRATE_AT_END, vibratePreference.isChecked)
                    Constants.PAUSE_ON_SCORE_CHANGE -> settingsEditor.putBoolean(Constants.PAUSE_ON_SCORE_CHANGE, pausePreference.isChecked)
                    Constants.KEEP_DEVICE_AWAKE -> settingsEditor.putBoolean(Constants.KEEP_DEVICE_AWAKE, awakePreference.isChecked)
                    Constants.POPUP_ON_SCORE -> settingsEditor.putBoolean(Constants.POPUP_ON_SCORE, popupPreference.isChecked)
                    Constants.VIBRATE_TIMER -> settingsEditor.putBoolean(Constants.VIBRATE_TIMER, vibrateTimerPreference.isChecked)
                    Constants.TOGGLE_DOUBLE_TOUCH -> settingsEditor.putBoolean(Constants.TOGGLE_DOUBLE_TOUCH, doubleTouchPreference.isChecked)
                    Constants.VOLUME_BUTTON_TIMER_TOGGLE -> settingsEditor.putBoolean(Constants.VOLUME_BUTTON_TIMER_TOGGLE, volumeButtonTogglePreference.isChecked)
                }
            }
            settingsEditor.apply()
        }


        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)

            if (view != null) {
                view!!.setBackgroundColor(Color.WHITE)
            }
            view!!.isClickable = true
        }

    }
}
