package com.android.susmita.ecalcharge_mercedez_android;

/**
 * Created by susmita on 9/25/2017.
 */

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

public class EVSmartCharging {

    public void startSmartChargingActivity(SingleCarActivity singleCarActivity, float fltEstTimeToChargeFromSOCminToFull, int SOCcurr, int SOCmin, String departureDateTime, String dateTimeFormat) {
        System.out.println("SOC got: "+SOCcurr);
        Double[] apxPrice = {1.0,2.0,3.0,4.0,5.0,6.9,7.0,8.0,
                1.0,2.0,3.0,4.0,5.0,6.9,7.0,8.0,
                1.0,2.0,1.0,4.0,5.0,0.9,7.0,8.0, 2.0 };
        String spreadsheetKey = "1dSyhVOt8sEpmopzNe-Vo5Fm-qIJX1Fa5E-HAZOFodj0";
        String date = "08/19/2017";
//        GetApxData getApxData = new GetApxData();
//        Double[] apxPrice = getApxData.getAPXprice(spreadsheetKey,date);
        for(Double apx:apxPrice){
            System.out.print(">>>>>>" + apx);
        }
        SimpleDateFormat sdf = new SimpleDateFormat(dateTimeFormat);
        DateFormat dateFormatter = new SimpleDateFormat(dateTimeFormat);
        Date d1 = new Date();

        System.out.println(">>>>>>>>>>>>>>>>>>>>> fltEstTimeToChargeFromSOCminToFull ="+fltEstTimeToChargeFromSOCminToFull);
        Date dtStartFlexibleCharge = d1;

        boolean print = true;

        if(SOCcurr < SOCmin) {
            System.out.println("Going inside if");

            if (print) {
                singleCarActivity.smartStatus.setText("Your charging started immediately as Current SOC is less than Minimum SOC.Smart Charging will start after current SOC reaches Minimum SOC");
            print = false;
            }
            //get the estimated time to charge from SOCcurr to SOCmin
            float fltEstTimeToCharge = singleCarActivity.findEstimatedChargingTime();
            float chargeTillSOCmin = SOCmin - SOCcurr;
            float chargeFromSOCcurrToFull = 100 - SOCcurr;
            float fltEstTimeToChargeTillSOCmin = fltEstTimeToCharge *  (chargeTillSOCmin / chargeFromSOCcurrToFull);

            float chargeFromSOCminToFull = 100 - SOCmin;
            fltEstTimeToChargeFromSOCminToFull =  fltEstTimeToCharge * (chargeFromSOCminToFull / chargeFromSOCcurrToFull);

            //add the estimated time to charge to current the time
            int intEstTimeToChargeTillSOCmin = (int) (fltEstTimeToChargeTillSOCmin) + 1;
            Date d2 = this.addMinutes(d1, intEstTimeToChargeTillSOCmin);

            String strD1 = sdf.format(addSeconds(d1,30));
            String strD2 = sdf.format(d2);

            //updating the flexible date start charge
            dtStartFlexibleCharge = d2;

            Date dtStart = null;
            Date dtStop = null;


            try {

                dtStart = dateFormatter.parse(strD1);
                dtStop = dateFormatter.parse(strD2);

            } catch (ParseException e1) {

                e1.printStackTrace();
            }

            // Now create the timer for StartCahrging and StopCharging
            Timer startTimer = new Timer();
            Timer stopTimer = new Timer();

            // Create the EVChargingTask
            EVChargingTask evChargingTaskStart = new EVChargingTask(singleCarActivity, startTimer, stopTimer, true);
            //EVChargingTask evChargingTaskStop = new EVChargingTask(singleCarActivity, startTimer, stopTimer, false);

            // Schedule the task
            startTimer.schedule(evChargingTaskStart, dtStart);
            //startTimer.schedule(evChargingTaskStop, dtStop);
        }
        else {


        }

        //Start Flexible Charge
        // Departure Time - Max End Flexible charge
        // Time Range

        float fltHours = (float) (fltEstTimeToChargeFromSOCminToFull/60.00);
        int intHours = (int) (fltEstTimeToChargeFromSOCminToFull/60.00);
        int slots = intHours + 1;
        if(fltHours - intHours > 0.5) {
            slots++;
        }
        Date endFlexibleCharge = null;
        try {
            endFlexibleCharge = dateFormatter.parse(departureDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int start_HH = dtStartFlexibleCharge.getHours();
        int end_HH = endFlexibleCharge.getHours();
        Double[] subprice = Arrays.copyOfRange(apxPrice, start_HH, end_HH);

        float completedChargedTime = 0.0f;
        for (int index = 1; index <= slots; index++) {

            if(completedChargedTime > fltEstTimeToChargeFromSOCminToFull)  {
                break;
            }

            int minIndex = this.getMinimum(subprice);


            int start_hour =  minIndex + start_HH+1;
            int end_hour = 0;
            int end_min = 0;
            if(completedChargedTime + 60.0 <= fltEstTimeToChargeFromSOCminToFull) {
                end_hour = start_hour + 1;
                end_min = 0;
                completedChargedTime += 60.0;
            }
            else {

                end_hour = start_hour;
                end_min = (int)(fltEstTimeToChargeFromSOCminToFull - completedChargedTime) + 1;
                completedChargedTime += end_min;
            }

            String start_date_time = sdf.format(dtStartFlexibleCharge);
            String start_date = start_date_time.substring(0, 10);

            String st_hour = (start_hour > 9) ? "" + start_hour : "0" + start_hour;
            String ed_hour = (end_hour > 9) ? "" + end_hour : "0" + end_hour;
            String ed_min = (end_min > 9) ? "" + end_min : "0" + end_min;

            if (print ==true){
                print = false;
                singleCarActivity.smartStatus.setText("Your smart charging will start at " + String.valueOf(st_hour)+ ":00 hrs");

            }

            subprice[minIndex] = Double.MAX_VALUE;

            String strD1 = start_date + " " + st_hour + ":00";
            String strD2 = start_date + " " + ed_hour + ":" + ed_min;

            Date dtStart = null;
            Date dtStop = null;


            try {

                dtStart = dateFormatter.parse(strD1);
                dtStop = dateFormatter.parse(strD2);

            } catch (ParseException e1) {

                e1.printStackTrace();
            }

            // Now create the timer for StartCahrging and StopCharging
            Timer startTimer = new Timer();
            Timer stopTimer = new Timer();

            // Create the EVChargingTask
            //EVChargingTask evChargingTaskStart = new EVChargingTask(singleCarActivity, startTimer, stopTimer, true);
            //EVChargingTask evChargingTaskStop = new EVChargingTask(singleCarActivity, startTimer, stopTimer, false);

            // Schedule the task
            //startTimer.schedule(evChargingTaskStart, dtStart);
            //startTimer.schedule(evChargingTaskStop, dtStop);
        }
    }

    public int getMinimum(Double[] array) {

        int minIndex = -1;
        double minValue = Double.MAX_VALUE;

        int index = -1;
        for(double value : array) {

            index++;

            if(value < minValue) {
                minValue = value;
                minIndex = index;
            }
        }

        return minIndex;
    }


    public Date addMinutes(Date d1, int minutes) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(d1);
        cal.add(Calendar.MINUTE, minutes);
        Date d2 = cal.getTime();

        return d2;
    }

    public Date addSeconds(Date d1, int sec) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(d1);
        cal.add(Calendar.SECOND, sec);
        Date d2 = cal.getTime();

        return d2;
    }

}