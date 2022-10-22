package com.example.todo;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimePicker extends DialogFragment {

    TextView txt;

    public TimePicker(TextView textView) {
        this.txt = textView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), timeSetListener, hour, minute, false);
        return timePickerDialog;
    }

    private void updateDisplay(String date){
        txt.setText(date);
    }

    private TimePickerDialog.OnTimeSetListener timeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {

                    Time time = new Time(hourOfDay, minute, 0);
                    SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");


                    String selectedTime = formatter.format(time);
                    updateDisplay(selectedTime);
                }
            };
}
