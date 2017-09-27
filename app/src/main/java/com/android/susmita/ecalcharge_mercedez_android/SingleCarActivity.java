package com.android.susmita.ecalcharge_mercedez_android;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.highmobility.hmkit.Command.Command;
import com.highmobility.hmkit.Command.CommandParseException;
import com.highmobility.hmkit.Command.Incoming.ChargeState;
import com.highmobility.hmkit.Command.Incoming.IncomingCommand;
import com.highmobility.hmkit.Error.DownloadAccessCertificateError;
import com.highmobility.hmkit.Error.TelematicsError;
import com.highmobility.hmkit.Manager;
import com.highmobility.hmkit.Telematics;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by susmita on 9/20/2017.
 */

public class SingleCarActivity extends Activity {

    static final int SOCMin = 20;
    static String TAG = "SingleCarActivity";
    static int curSoc;
    byte[] vehicleSerial;
    @BindView(R.id.soc_button)
    Button socButton;
    @BindView(R.id.soc_textView)
    TextView socTextView;
    @BindView(R.id.switch2)
    Switch switchButton;
    boolean charging;
    @BindView(R.id.time_textView)
    TextView timeTextView;
    @BindView(R.id.scheduledStatus)
    TextView scheduledStatus;
    @BindView(R.id.smartStatus)
    TextView smartStatus;
    @BindView(R.id.scheduled_startTv)
    TextView scheduledStartTv;
    @BindView(R.id.scheduled_endTv)
    TextView scheduledEndTv;
    @BindView(R.id.scheduled_btn)
    Button scheduledBtn;
    @BindView(R.id.smart_btn)
    Button smartBtn;
    @BindView(R.id.smart_tv)
    TextView smartDepartureTv;
    float soc;
    TimePicker timePicker1;
    TimePicker timePicker2;
    TimePicker timePicker3;
    private float time;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_car);
        ButterKnife.bind(this);
        getInstance();
        downloadCertificate();
        timePicker1 = findViewById(R.id.timePicker1);
        timePicker1.setIs24HourView(true);
        timePicker2 = findViewById(R.id.timePicker2);
        timePicker2.setIs24HourView(true);
        timePicker3 = findViewById(R.id.timePicker2);
        timePicker3.setIs24HourView(true);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.clearCheck();
        switchButton.setOnCheckedChangeListener((cb, isChecked) -> {
            if (isChecked) {
                charging = false;
//                startCharging();
            } else {
                charging = true;
//                startCharging();
                stopCharging();
            }
        });
        scheduledBtn.setOnClickListener(view -> {
                    startScheduledCharging();
                }
        );
        socButton.setOnClickListener(view ->
                {
                    findSoc();

                }
        );
        radioGroup.setOnCheckedChangeListener(this::selectChargingOptions);
        smartBtn.setOnClickListener(view -> startSmartCharging());
    }

    private void downloadCertificate() {
        Manager.getInstance().downloadCertificate("AI7C2xGAR78Xq4jQo6e4c6_wIWpBg4IZslgREwtztCrBozXnoixi6tHXbtN5tR3Sc3I_Ge8lXSHM3AQVSrwr3FKTkkm_PWIxfKSp1j2Ss0liOHq2aTFCyOCAWZ6w2yTfhg", new Manager.DownloadCallback() {
            @Override
            public void onDownloaded(byte[] bytes) {
                Log.d(TAG, "Access granted: ");
                vehicleSerial = bytes;
            }

            @Override
            public void onDownloadFailed(DownloadAccessCertificateError downloadAccessCertificateError) {
                Log.d(TAG, "onDownloadFailed" + downloadAccessCertificateError.getType() + downloadAccessCertificateError.getMessage());
            }
        });
    }

    public void getInstance() {
        Manager.getInstance().initialize(
                "dGVzdDzJFTy4zTnBNmCoyXGqO65YY+4JFpdYA0aJvRB4zyjrOHTLFb3VB2SLvvOStdRi0hXEjv5dJ1zBZXne7HEEFB/hnHaTFMTB/sdm5yoQREJgdt9osCwgLiwWdsmQ0f2G5MgYK8URdbxiAiZNVK14G28BCB0ITmsIkZAEmcbCRGZeN2aLBMc8FoPxvPdpEybJKaXSCGnd",
                "bMrg2kovov8xOKpC7HAsxAWx4fxcPyysj+9Y6XvOGZE=",
                "mtgEqamle56rOE2oKVyh3IJ/5hmOhEyhT/I8yEuJ+MWAUhWR4qYR6TLPQTxJ1amdgApRLCXL/RJyaA0PDJaxEw==",
                getApplicationContext()
        );
    }


    public float findSoc() {
        final Telematics telematics = Manager.getInstance().getTelematics();
        byte[] command = Command.Charging.getChargeState();
        telematics.sendCommand(command, vehicleSerial, new Telematics.CommandCallback() {
            @Override
            public void onCommandResponse(byte[] bytes) {
                try {
                    IncomingCommand incomingCommand = IncomingCommand.create(bytes);
                    if (incomingCommand.is(Command.Charging.CHARGE_STATE)) {
                        ChargeState state = (ChargeState) incomingCommand;
                        Log.d(TAG, "Battery: " + state.getBatteryLevel());
                        soc = state.getBatteryLevel() * 100;
                        socTextView.setText(String.valueOf(state.getBatteryLevel() * 100) + "%");
                    }
                } catch (CommandParseException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }

            @Override
            public void onCommandFailed(TelematicsError telematicsError) {
                Log.d(TAG, "findSOC Error " + telematicsError.getMessage());
            }
        });
        return soc;
    }

    public void stopCharging() {
        byte[] command = Command.Charging.startCharging(false);
        final Telematics telematics = Manager.getInstance().getTelematics();
        telematics.sendCommand(command, vehicleSerial, new Telematics.CommandCallback() {

            @Override
            public void onCommandResponse(byte[] bytes) {
                try {
                    IncomingCommand incomingCommand = IncomingCommand.create(bytes);
                    if (incomingCommand.is(Command.Charging.CHARGE_STATE)) {
                        ChargeState state = (ChargeState) incomingCommand;
                        timeTextView.setVisibility(View.GONE);
                        switchButton.setChecked(false);
                        Log.d(TAG, "Stop Function" + String.valueOf((state.getChargingState())));
                    }
                } catch (CommandParseException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onCommandFailed(TelematicsError telematicsError) {
                Log.d(TAG, "Stop function Error " + telematicsError.getMessage());
            }
        });
    }

    public void startCharging() {
        Log.d(TAG, "startCharging() called at time = " + new Date().toString());
        byte[] command;
        final Telematics telematics = Manager.getInstance().getTelematics();
        if (charging) {
            command = Command.Charging.startCharging(false);

        } else {
            command = Command.Charging.startCharging(true);

        }
        telematics.sendCommand(command, vehicleSerial, new Telematics.CommandCallback() {
            @Override
            public void onCommandResponse(byte[] bytes) {
                try {
                    IncomingCommand incomingCommand = IncomingCommand.create(bytes);
                    if (incomingCommand.is(Command.Charging.CHARGE_STATE)) {
                        ChargeState chargeState = (ChargeState) incomingCommand;
                        if ((String.valueOf(chargeState.getChargingState())).equals("DISCONNECTED")) {
                            charging = false;
                            switchButton.setChecked(false);
//                        radioGroup.clearCheck();
                            timeTextView.setVisibility(View.GONE);

                        } else {
                            charging = true;
                            switchButton.setChecked(true);
                            findEstimatedChargingTime();
                        }
                    }

                } catch (CommandParseException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onCommandFailed(TelematicsError telematicsError) {
                Log.d(TAG, "Start Charging Error " + telematicsError.getMessage());
            }

        });

        /*
        // Now create the timer for stopCharging;
        Timer stopTimer = new Timer();
        // Create the EVChargingTask
        EVStopChargingTask evStopChargingTask = new EVStopChargingTask(this, stopTimer);
        // Schedule the task
        Date dtStart = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(dtStart);
        cal.add(Calendar.MINUTE, 10);
        dtStart = cal.getTime();
        System.out.println("Assiging the stop charging event at Time = " + dtStart.toString());
        stopTimer.schedule(evStopChargingTask, dtStart, 10000000);

        */
    }


    public void selectChargingOptions(RadioGroup radioGroup1, int i) {
        RadioButton rb = radioGroup1.findViewById(i);
        int index = radioGroup1.indexOfChild(rb);
        Log.d(TAG, String.valueOf(index));
        switch (index) {
            case 0: {
                immediateCharging();
                break;
            }
            case 2: {
                scheduledCharging();
                break;
            }
            case 7: {
                smartCharging();
                break;
            }
        }
    }

    public void immediateCharging() {
        startCharging();
        timeTextView.setVisibility(View.VISIBLE);
        timePicker1.setVisibility(View.GONE);
        timePicker2.setVisibility(View.GONE);
        scheduledStartTv.setVisibility(View.GONE);
        scheduledEndTv.setVisibility(View.GONE);
        scheduledBtn.setVisibility(View.GONE);
        smartBtn.setVisibility(View.GONE);
        timePicker3.setVisibility(View.GONE);
        smartDepartureTv.setVisibility(View.GONE);
    }

    public void scheduledCharging() {
        timeTextView.setVisibility(View.GONE);
        timePicker3.setVisibility(View.GONE);
        smartBtn.setVisibility(View.GONE);
        smartDepartureTv.setVisibility(View.GONE);
        scheduledStartTv.setVisibility(View.VISIBLE);
        scheduledEndTv.setVisibility(View.VISIBLE);
        timePicker1.setVisibility(View.VISIBLE);
        timePicker2.setVisibility(View.VISIBLE);
        scheduledBtn.setVisibility(View.VISIBLE);
        Log.d(TAG, "scheduled");
        scheduledBtn.setOnClickListener(view -> startScheduledCharging());
    }

    public void startScheduledCharging() {
        timePicker1.setVisibility(View.GONE);
        timePicker2.setVisibility(View.GONE);
        scheduledStartTv.setVisibility(View.GONE);
        scheduledEndTv.setVisibility(View.GONE);
        scheduledBtn.setVisibility(View.GONE);
        Log.d(TAG, "startTime");
//        String startTime = scheduledStartEt.getText().toString();
//        String endTime = scheduledEndEt.getText().toString();
//        String startTime = "1:00";
//      String endTime = "12:00";
        Date currDate = new Date();
//        String[] st_time = startTime.split(":");
//        String[] end_time = endTime.split(":");
//        String st_HH = st_time[0];
//        String st_mm = st_time[1];
//        String end_HH = end_time[0];
//        String end_mm = end_time[1];
        String st_HH = String.valueOf(timePicker1.getHour());
        String st_mm = String.valueOf(timePicker1.getMinute());
        String end_HH = String.valueOf(timePicker2.getHour());
        String end_mm = String.valueOf(timePicker2.getMinute());
        Log.d(TAG, st_HH + ":" + st_mm + " -----" + end_HH + ":" + end_mm);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String stCurrDate = sdf.format(currDate);
        String formatDtTm = "yyyy-MM-dd HH:mm";
        String startDtTm = stCurrDate + " " + st_HH + ":" + st_mm;
        String stopDtTm = stCurrDate + " " + end_HH + ":" + end_mm;
        scheduledStatus.setVisibility(View.VISIBLE);
        int randNum = (int) (Math.random() * 50) + 50;
        System.out.println(randNum + "randnum");
        scheduledStatus.setText("Your estimated SOC at " + end_HH + ":" + end_mm + " will be " + randNum + "%");
        Log.d(TAG, "startDtTm = " + startDtTm + " - stopDtTm = " + stopDtTm);
        DateFormat dateFormatter = new SimpleDateFormat(formatDtTm);
        Date dtStart = null;
        try {
            dtStart = dateFormatter.parse(startDtTm);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date dtStop = null;
        try {
            dtStop = dateFormatter.parse(stopDtTm);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Now create the timer for StartCahrging and StopCharging
        Timer startTimer = new Timer();
        Timer stopTimer = new Timer();
        // Create the EVChargingTask
        EVChargingTask evChargingTaskStart = new EVChargingTask(this, startTimer, stopTimer, true);
        EVChargingTask evChargingTaskStop = new EVChargingTask(this, startTimer, stopTimer, false);
        // Schedule the task
        startTimer.schedule(evChargingTaskStart, dtStart);
        startTimer.schedule(evChargingTaskStop, dtStop);
    }

    public void smartCharging() {
        Log.d(TAG, "smart");
        timeTextView.setVisibility(View.GONE);
//        scheduledStartEt.setVisibility(View.GONE);
//        scheduledEndEt.setVisibility(View.GONE);
        timePicker1.setVisibility(View.GONE);
        timePicker2.setVisibility(View.GONE);
        scheduledStartTv.setVisibility(View.GONE);
        scheduledEndTv.setVisibility(View.GONE);
        scheduledBtn.setVisibility(View.GONE);
        smartBtn.setVisibility(View.VISIBLE);
        timePicker3.setVisibility(View.VISIBLE);
        smartDepartureTv.setVisibility(View.VISIBLE);
    }


    public void startSmartCharging() {
        timePicker3.setVisibility(View.GONE);
        smartStatus.setVisibility(View.VISIBLE);
        smartBtn.setVisibility(View.GONE);
        smartDepartureTv.setVisibility(View.GONE);
        Log.d(TAG, "smartCharging Started");
        String dep_HH = String.valueOf(timePicker3.getHour());
        String dep_mm = String.valueOf(timePicker3.getMinute());
        String departureTime = dep_HH + ":" + dep_mm;
        //int currentSOC = (int) (findSoc() * 100.0);
//        int currentSOC = (int)soc;
        Log.d(TAG, "SOC inside smart" + String.valueOf(soc));
        int currentSOC = (int) soc;
        Log.d(TAG, "currentSOC = " + currentSOC);
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(dt);
        float estimatedTime = time;
        String departureDateTime = strDate + " " + departureTime;
        Log.d(TAG, "departureDateTime =" + departureDateTime);
        String dateTimeFormat = "yyyy-MM-dd HH:mm";
        EVSmartCharging evs = new EVSmartCharging();
        evs.startSmartChargingActivity(this, estimatedTime, currentSOC, SOCMin, departureDateTime, dateTimeFormat);

    }


    public float findEstimatedChargingTime() {
        timeTextView.setVisibility(View.VISIBLE);
        Log.d(TAG, "findestimate called");
        final Telematics telematics = Manager.getInstance().getTelematics();
        byte[] command = Command.Charging.getChargeState();
        telematics.sendCommand(command, vehicleSerial, new Telematics.CommandCallback() {
            @Override
            public void onCommandResponse(byte[] bytes) {
                Log.d(TAG, "response got");
                try {
                    IncomingCommand incomingCommand = IncomingCommand.create(bytes);
                    Log.d(TAG, "inside try");
                    if (incomingCommand.is(Command.Charging.CHARGE_STATE)) {
                        ChargeState state = (ChargeState) incomingCommand;
                        Log.d(TAG, "Time: " + state.getTimeToCompleteCharge());
                        time = state.getTimeToCompleteCharge();
                        timeTextView.setText("The estimated time to complete charging is: " + String.valueOf(time) + " minutes");
                    }
                } catch (CommandParseException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                    Log.d(TAG, "Sorry");
                }
                Log.d(TAG, "tried");
            }

            @Override
            public void onCommandFailed(TelematicsError telematicsError) {
                Log.d(TAG, "find estimate error " + telematicsError.getMessage());
            }
        });
        return time;
    }

}
