package com.maxchehab.groupfit;

import android.app.Dialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by maxchehab on 6/10/17.
 */

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd");
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,day);

        String weekDay = sdf.format(calendar.getTime());

        SimpleDateFormat format1 = new SimpleDateFormat("MM-dd-yyy");



        ((CreateNewEventActivity)getActivity()).setDate(weekDay,format1.format(calendar.getTime()));

    }
}