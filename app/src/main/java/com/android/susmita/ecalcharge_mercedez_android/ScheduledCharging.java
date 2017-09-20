package com.android.susmita.ecalcharge_mercedez_android;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by susmita on 9/10/2017.
 */

public class ScheduledCharging {

    public static void startScheduledCharging(String start1, String end1) {
//        String rawDate = EditText.getText().toString();
        DateFormat format = new SimpleDateFormat("expected format", Locale.US);
        Date start= null;
        Date end = null;
        try {
            start = format.parse(start1);
            end = format.parse(end1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date currentDate = new Date();
        if (start.after(currentDate)) {
        }
//if editText date is after
        else {
//if editText date is not after
        }
    }
}
