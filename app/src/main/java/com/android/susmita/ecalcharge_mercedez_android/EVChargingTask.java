package com.android.susmita.ecalcharge_mercedez_android;

/**
 * Created by susmita on 9/25/2017.
 */

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class EVChargingTask  extends TimerTask{

    private Timer startTimer;
    private Timer stopTimer;
    private boolean isCharge;
    private SingleCarActivity singleCarActivity;

    public EVChargingTask(SingleCarActivity singleCarActivity, Timer startTimer, Timer stopTimer, boolean isCharge) {

        Date dt = new Date();
        System.out.print("Time [" + dt.toString() + "] - ");
        System.out.println("EVCharging Initialized ... ");

        this.singleCarActivity = singleCarActivity;
        this.startTimer = startTimer;
        this.stopTimer = stopTimer;
        this.isCharge = isCharge;
    }

    @Override
    public void run() {

        if(isCharge) {

            Date dt = new Date();
            System.out.print("Time [" + dt.toString() + "] - ");
            System.out.println("Calling startCharging() ... ");
            this.startCharging();
        }
        else {

            Date dt = new Date();
            System.out.print("Time [" + dt.toString() + "] - ");
            System.out.println("Calling stopCharging() ... ");
            this.stopCharging();
        }

    }

    private void startCharging() {

        Date dt = new Date();
        System.out.print("Time [" + dt.toString() + "] - ");
        System.out.println("Charging Started ... ");

        // Call the function for start charging here
        //SingleCarActivity objSingleCar = new SingleCarActivity();
        singleCarActivity.startCharging();


    }

    private void stopCharging() {

        Date dt = new Date();
        System.out.print("Time [" + dt.toString() + "] - ");
        System.out.println("Charging Stopped ... ");

        // Call the function for stop charging here
        singleCarActivity.startCharging();
        startTimer.cancel();
        stopTimer.cancel();

        Date dt2 = new Date();
        System.out.print("Time [" + dt2.toString() + "] - ");
        System.out.println("Everything Stopped ... ");
    }

}
