package com.kfang.fencemelibrary;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.NumberPicker;

import com.kfang.fencemelibrary.main.MainContract;

import java.util.Locale;

/**
 * Time Picker Fragment for setting timer
 */


public class TimePickerFragment extends DialogFragment {

    MainContract.MainPresenter presenter;

    public static TimePickerFragment newInstance(int title, MainContract.MainPresenter presenter) {
        TimePickerFragment fragment = new TimePickerFragment();
        Bundle args = new Bundle();
        fragment.presenter = presenter;
        args.putInt("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View pickerView = View.inflate(getActivity(), R.layout.time_picker, null);
        final NumberPicker minutesPicker = (NumberPicker) pickerView.findViewById(R.id.minutes_picker);
        final NumberPicker secondsPicker = (NumberPicker) pickerView.findViewById(R.id.seconds_picker);

        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);
        minutesPicker.setValue(presenter.getCurrentTime() / 60);
        secondsPicker.setMaxValue(59);
        secondsPicker.setMinValue(0);
        secondsPicker.setValue(presenter.getCurrentTime() % 60);
        secondsPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format(Locale.getDefault(), "%02d", value);

            }
        });


        builder.setView(pickerView)
                .setPositiveButton(R.string.button_set_timer, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setTimer(minutesPicker.getValue(), secondsPicker.getValue());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TimePickerFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    public void setTimer(int minutes, int seconds) {
        if (seconds == 0 && minutes == 0) {
            presenter.setTimer(presenter.getBoutLength() * 60);
        } else {
            presenter.setTimer(seconds + minutes * 60);
        }
    }

}