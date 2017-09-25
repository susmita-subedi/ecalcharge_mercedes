package com.android.susmita.ecalcharge_mercedez_android;

/**
 * Created by susmita on 9/25/2017.
 */

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

public class EVApp {

    public void charging(String formatDtTm, String startDtTm, String stopDtTm) throws ParseException {

        // Example:
        // formatDtTm = "yyyy-MM-dd HH:mm:ss"
        // startDtTm = "2017-09-21 15:52:10"
        // stopDtTm = "2017-09-21 20:52:10"

        DateFormat dateFormatter = new SimpleDateFormat(formatDtTm);

        Date dtStart = dateFormatter.parse(startDtTm);
        Date dtStop = dateFormatter.parse(stopDtTm);

        // Now create the timer for StartCahrging and StopCharging
        Timer startTimer = new Timer();
        Timer stopTimer = new Timer();

        // Create the EVChargingTask
        EVChargingTask evChargingTaskStart = new EVChargingTask(null, startTimer, stopTimer, true);
        EVChargingTask evChargingTaskStop = new EVChargingTask(null, startTimer, stopTimer, false);

        // Schedule the task
        startTimer.schedule(evChargingTaskStart, dtStart);
        startTimer.schedule(evChargingTaskStop, dtStop);

    }

    public static void main(String[] args) {

        EVApp app = new EVApp();

        String formatDtTm = "yyyy-MM-dd HH:mm:ss";
        String startDtTm = "2017-09-21 16:21:10";
        String stopDtTm = "2017-09-21 16:23:10";

        try {

            app.charging(formatDtTm, startDtTm, stopDtTm);
        }
        catch (ParseException e) {

            e.printStackTrace();
        }
    }
}