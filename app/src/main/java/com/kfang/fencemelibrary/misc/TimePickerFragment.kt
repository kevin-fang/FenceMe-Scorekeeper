package com.kfang.fencemelibrary.misc

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import com.kfang.fencemelibrary.R
import com.kfang.fencemelibrary.presentation.MainContract
import kotlinx.android.synthetic.main.time_picker.view.*
import java.util.*

/**
 * Time Picker Fragment for setting timer in the main screen
 */


class TimePickerFragment : DialogFragment() {

    lateinit var presenter: MainContract.MainPresenter
    lateinit var mainDialog: Dialog

    private fun setOneMin() {
        setTimer(1, 0)
        mainDialog.dismiss()
    }

    private fun setThreeMin() {
        setTimer(3, 0)
        mainDialog.dismiss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val pickerView = View.inflate(activity, R.layout.time_picker, null)

        // set initial values
        pickerView.minutes_picker.minValue = 0
        pickerView.minutes_picker.maxValue = 59
        pickerView.minutes_picker.value = presenter.currentSeconds / 100 / 60
        pickerView.seconds_picker.maxValue = 59
        pickerView.seconds_picker.minValue = 0
        pickerView.seconds_picker.value = presenter.currentSeconds / 100 % 60
        pickerView.seconds_picker.setFormatter { value -> String.format(Locale.getDefault(), "%02d", value) }

        // set shortcuts
        pickerView.one_minute_shortcut.setOnClickListener { setOneMin() }
        pickerView.three_minutes_shortcut.setOnClickListener { setThreeMin() }

        builder.setView(pickerView)
                .setPositiveButton(R.string.button_set_timer) { _, _ -> setTimer(pickerView.minutes_picker.value, pickerView.seconds_picker.value) }
                .setNegativeButton(R.string.cancel) { _, _ -> this@TimePickerFragment.dialog.cancel() }

        mainDialog = builder.create()
        return mainDialog
    }

    private fun setTimer(minutes: Int, seconds: Int) {
        if (seconds == 0 && minutes == 0) {
            presenter.setTimerSeconds(presenter.boutLengthMinutes * 60)
        } else {
            presenter.setTimerSeconds((seconds + minutes * 60))
        }
    }

    companion object {

        fun newInstance(title: Int, presenter: MainContract.MainPresenter): TimePickerFragment {
            val fragment = TimePickerFragment()
            val args = Bundle()
            fragment.presenter = presenter
            args.putInt("title", title)
            fragment.arguments = args
            return fragment
        }
    }
}