package com.android.susmita.ecalcharge_mercedez_android;


import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class EVStopChargingTask  extends TimerTask {

    private Timer stopTimer;
    private SingleCarActivity singleCarActivity;

    public EVStopChargingTask(SingleCarActivity singleCarActivity, Timer stopTimer) {

        Date dt = new Date();
        System.out.print("Time [" + dt.toString() + "] - ");
        System.out.println("EVStopChargingTask Initialized ... ");

        this.singleCarActivity = singleCarActivity;
        this.stopTimer = stopTimer;
    }

    @Override
    public void run() {

        stopCharging();
    }

    private void stopCharging() {

        float soc = singleCarActivity.findSoc();

        System.out.println("Print the soc >>> " + soc);

        if(soc >= 0.9) {

            Date dt = new Date();
            System.out.print("Time [" + dt.toString() + "] - ");
            System.out.println("Charging Stopped as soc reached 100%... ");

            // Call the function for stop charging here
            singleCarActivity.stopCharging();
            stopTimer.cancel();
        }
        else {

            System.out.println(">>>> soc = " + soc);
        }
    }

}

