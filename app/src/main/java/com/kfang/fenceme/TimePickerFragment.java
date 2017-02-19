package com.kfang.fenceme;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

/**
 * Time Picker Fragment for setting timer
 */


public class TimePickerFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View pickerView = inflater.inflate(R.layout.time_picker, null);
        final NumberPicker minutesPicker = (NumberPicker) pickerView.findViewById(R.id.minutes_picker);
        final NumberPicker secondsPicker = (NumberPicker) pickerView.findViewById(R.id.seconds_picker);

        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(60);
        minutesPicker.setValue((int) MainActivity.mCurrentTime / 1000 / 60);
        secondsPicker.setMaxValue(59);
        secondsPicker.setMinValue(0);
        secondsPicker.setValue((int) MainActivity.mCurrentTime / 1000 % 60);
        secondsPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%02d", value);

            }
        });


        builder.setView(pickerView)
                .setPositiveButton(R.string.set_timer, new DialogInterface.OnClickListener() {
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

    public static TimePickerFragment newInstance(int title) {
        TimePickerFragment fragment = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    public void setTimer(int minutes, int seconds) {
        MainActivity.mCurrentTime = seconds * 1000 + minutes * 60000;
        Intent setTimer = new Intent(getContext(), TimerService.class);
        setTimer.putExtra("TOGGLE", TimerService.SET_TIMER);
        //Toast.makeText(getContext(), "" + MainActivity.mCurrentTime, Toast.LENGTH_SHORT).show();
        getActivity().startService(setTimer);
    }

}