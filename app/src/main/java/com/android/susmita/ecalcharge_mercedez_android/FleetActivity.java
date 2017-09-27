package com.android.susmita.ecalcharge_mercedez_android;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.highmobility.hmkit.Command.Command;
import com.highmobility.hmkit.Command.CommandParseException;
import com.highmobility.hmkit.Command.Incoming.ChargeState;
import com.highmobility.hmkit.Command.Incoming.IncomingCommand;
import com.highmobility.hmkit.Error.DownloadAccessCertificateError;
import com.highmobility.hmkit.Error.TelematicsError;
import com.highmobility.hmkit.Manager;
import com.highmobility.hmkit.Telematics;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by susmita on 9/20/2017.
 */

public class FleetActivity extends Activity {
    static String TAG = "FleetActivity";
    byte[] vehicleSerial;
    byte[] vehicleSerial1;
    byte[] vehicleSerial2;
    byte[] vehicleSerial3;
    @BindView(R.id.car1soc)
    TextView car1socTv;
    @BindView(R.id.car2soc)
    TextView car2socTv;
    @BindView(R.id.car3soc)
    TextView car3socTv;
    @BindView(R.id.car1status)
    TextView car1statusTv;
    @BindView(R.id.car2status)
    TextView car2statusTv;
    @BindView(R.id.car3status)
    TextView car3statusTv;
    @BindView(R.id.car1ChargingTime)
    TextView car1chargingTime;
    @BindView(R.id.car2ChargingTime)
    TextView car2chargingTime;
    @BindView(R.id.car3ChargingTime)
    TextView car3chargingTime;
    @BindView(R.id.car1Range)
    TextView car1range;
    @BindView(R.id.car2Range)
    TextView car2range;
    @BindView(R.id.car3Range)
    TextView car3range;
    @BindView(R.id.fleetStatusButton)
    Button fleetStatusBtn;
    @BindView(R.id.car1)
    Button car1Btn;
    @BindView(R.id.car2)
    Button car2Btn;
    @BindView(R.id.car3)
    Button car3Btn;
    float soc = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fleet);
        ButterKnife.bind(this);
        getInstance();
        downloadCertificate1();
        downloadCertificate2();
        downloadCertificate3();
        car1Btn.setOnClickListener(e -> {
            Log.d(TAG, "multiple2");
            Log.d(TAG, "multiple");
            findSoc1("car1");
            getStatus("car1");
        });
        car2Btn.setOnClickListener(e -> {
            findSoc1("car2");
            getStatus("car2");
        });
        car3Btn.setOnClickListener(e -> {
            findSoc1("car3");
            getStatus("car3");
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

    private void downloadCertificate1() {
        Manager.getInstance().downloadCertificate("f-EyOjLDPTiiKGNiW4cMr1wJu73ecDX7zysapWWMpYuvJRLJ3g1HhRM9vkkswBW2BEmLjaH7nLlenMUOqnJsuzwZ_lOSbEy5YdFbB0NJTwPacrLJIwV0sXCm_1I8juDG7Q", new Manager.DownloadCallback() {
//        Manager.getInstance().downloadCertificate(key, new Manager.DownloadCallback() {

            @Override
            public void onDownloaded(byte[] serial) {
                Log.d(TAG, "Access granted: ");
                vehicleSerial1 = serial;
            }

            @Override
            public void onDownloadFailed(DownloadAccessCertificateError downloadAccessCertificateError) {
                Log.d(TAG, "onDownloadFailed" + downloadAccessCertificateError.getType() + downloadAccessCertificateError.getMessage());
            }
        });
    }

    private void downloadCertificate2() {
        Manager.getInstance().downloadCertificate("uU7fYvjF2KfPPe0qZbyKH-FfHlvj1fKAcDjavftFTIWD1Slwr8ZzLbmJLsLjxvWZUa71f8X9TLtcXTIEvcn19k8mTE_K9Ip4JRXA0tqj6V72xlAntUcA-PJTYM2BQjAhwg", new Manager.DownloadCallback() {
            @Override
            public void onDownloaded(byte[] serial) {
                Log.d(TAG, "Access granted: ");
                vehicleSerial2 = serial;

            }

            @Override
            public void onDownloadFailed(DownloadAccessCertificateError error) {
            }
        });
    }

    private void downloadCertificate3() {
        Manager.getInstance().downloadCertificate("2ICXgt4lw9dTMiujytNcqswvo3Ca7zdQmEgsRHJqKNStiStDHSZ_HYUJVqUzrWxRmqEo9S0pe08TV9wuo7XSuyDVJ5LS7VonvjGEk80ZTP6wPT7pXj-PwPhJdWltgUOQeg", new Manager.DownloadCallback() {
            @Override
            public void onDownloaded(byte[] serial) {
                Log.d(TAG, "Access granted: ");
                vehicleSerial3 = serial;

            }

            @Override
            public void onDownloadFailed(DownloadAccessCertificateError error) {
            }
        });
    }

    public void findSoc1(String car) {
        Telematics telematics = Manager.getInstance().getTelematics();
        byte[] command = Command.Charging.getChargeState();
        if (car == "car1") {
            vehicleSerial = vehicleSerial1;
        } else if (car == "car2") {
            vehicleSerial = vehicleSerial2;
        } else {
            vehicleSerial = vehicleSerial3;
        }
        telematics.sendCommand(command, vehicleSerial, new Telematics.CommandCallback() {
            @Override
            public void onCommandResponse(byte[] bytes) {
                try {
                    IncomingCommand incomingCommand = IncomingCommand.create(bytes);
                    if (incomingCommand.is(Command.Charging.CHARGE_STATE)) {
                        ChargeState state = (ChargeState) incomingCommand;
                        soc = state.getBatteryLevel() * 100;
                        if (car == "car1") {
                            Log.d(TAG, "Battery Level of Car 1: " + state.getBatteryLevel());
                            car1socTv.setText(String.valueOf(soc));
                            car1statusTv.setText(String.valueOf(state.getChargingState()));
                            car1chargingTime.setText(String.valueOf(state.getTimeToCompleteCharge()));
                            car1range.setText(String.valueOf(state.getEstimatedRange()));
                        } else if (car == "car2") {
                            Log.d(TAG, "Battery Level of Car 2: " + state.getBatteryLevel());
                            car2socTv.setText(String.valueOf(soc));
                            car2statusTv.setText(String.valueOf(state.getChargingState()));
                            car2chargingTime.setText(String.valueOf(state.getTimeToCompleteCharge()));
                            car2range.setText(String.valueOf(state.getEstimatedRange()));
                        } else if (car == "car3") {
                            Log.d(TAG, "Battery Level of Car 3: " + state.getBatteryLevel());
                            car3socTv.setText(String.valueOf(soc));
                            car3statusTv.setText(String.valueOf(state.getChargingState()));
                            car3chargingTime.setText(String.valueOf(state.getTimeToCompleteCharge()));
                            car3range.setText(String.valueOf(state.getEstimatedRange()));

                        }
                    }
                } catch (CommandParseException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }

            @Override
            public void onCommandFailed(TelematicsError e) {
                Log.e(TAG, "onCommandFailed()" + e.getMessage());
            }
        });
    }

    public void getStatus(String car) {
        Telematics telematics = Manager.getInstance().getTelematics();
        byte[] command = Command.Charging.getChargeState();
        if (car == "car1") {
            vehicleSerial = vehicleSerial1;
        } else if (car == "car2") {
            vehicleSerial = vehicleSerial2;
        } else {
            vehicleSerial = vehicleSerial3;
        }
        telematics.sendCommand(command, vehicleSerial, new Telematics.CommandCallback() {
            @Override
            public void onCommandResponse(byte[] bytes) {
                try {
                    IncomingCommand incomingCommand = IncomingCommand.create(bytes);
                    if (incomingCommand.is(Command.Charging.CHARGE_STATE)) {
                        ChargeState state = (ChargeState) incomingCommand;
                        soc = state.getBatteryLevel() * 100;
                        if (car == "car1") {
                            Log.d(TAG, "Status of Car 1: " + state.getChargingState());
                            car1statusTv.setText(String.valueOf(soc));
                        } else if (car == "car2") {
                            Log.d(TAG, "Status of Car 2: " + state.getChargingState());
                            car2statusTv.setText(String.valueOf(soc));
                        } else if (car == "car3") {
                            Log.d(TAG, "Status of Car 3: " + state.getChargingState());
                            car3statusTv.setText(String.valueOf(soc));

                        }
                    }
                } catch (CommandParseException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }

            @Override
            public void onCommandFailed(TelematicsError e) {
                Log.e(TAG, "onCommandFailed()" + e.getMessage());
            }
        });
    }

}
