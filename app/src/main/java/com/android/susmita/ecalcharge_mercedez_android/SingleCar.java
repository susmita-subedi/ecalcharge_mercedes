package com.android.susmita.ecalcharge_mercedez_android;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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

public class SingleCar extends Activity {

    static String TAG = "SingleCarActivity";
    byte[] vehicleSerial;
    @BindView(R.id.soc_button)
    Button socButton;
    @BindView(R.id.soc_textView)
    TextView socTextView;
    @BindView(R.id.charge_button)
    Button chargeButton;
    boolean charging;
    @BindView(R.id.time_textView)
    TextView timeTextView;
    @BindView(R.id.immediate_tv)
    TextView immediateTv;
    @BindView(R.id.scheduled_tv)
    TextView scheduledTv;
    @BindView(R.id.scheduled_tv2)
    TextView scheduledTv2;
    @BindView(R.id.scheduled_btn)
    Button scheduledBtn;
    private float time;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_car);
        ButterKnife.bind(this);
        getInstance();
        downloadCertificate();
        chargeButton.setOnClickListener(view -> startCharging());
        socButton.setOnClickListener(view -> findSoc());
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.clearCheck();
        radioGroup.setOnCheckedChangeListener(this::selectChargingOptions);
        scheduledBtn.setOnClickListener(view -> Toast.makeText(getBaseContext(), "Submit", Toast.LENGTH_SHORT).show());

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
                "dGVzdDzJFTy4zTnBNmCoyaefikxfOZDIjH8dydaPdoEqvSt4FHxIJRRIZ9fNwhCzJbujOeZEdgLCdvl4IvMzPJ5poR8kugiDyqBEiKPnn+PcNPFWI9akQFYwQC5Yd7aqxhl+/zA/4fMUUYipZTZPqRvaN2r888dzpTg7Dml/o6x2BXffbSX6gvHGPIIvcK5xpZ6dDEMUSwVj",
                "Y+dblf8NYzYzUn2ZGERMtf+FKx6xD7/pmTHqMxGwSN0=",
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
                        socTextView.setText(String.valueOf(state.getBatteryLevel() * 100));
                    }
                } catch (CommandParseException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }

            @Override
            public void onCommandFailed(TelematicsError telematicsError) {
            }
        });
        return time;
    }

    public void startCharging() {
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
                    ChargeState chargeState = (ChargeState) incomingCommand;
//                    socTextView.setText(String.valueOf(chargeState.getChargingState()));
                    if ((String.valueOf(chargeState.getChargingState())).equals("DISCONNECTED")) {
                        charging = false;
                        chargeButton.setText(R.string.turn_on);
                        timeTextView.setVisibility(View.GONE);

                    } else {
                        charging = true;
                        chargeButton.setText(R.string.turn_off);
                        findEstimatedChargingTime();
                    }

                } catch (CommandParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCommandFailed(TelematicsError telematicsError) {
            }

        });
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
            case 4: {
                smartCharging();
                break;
            }
        }
    }

    public void immediateCharging() {
        startCharging();
        immediateTv.setVisibility(View.VISIBLE);
        scheduledTv.setVisibility(View.GONE);
        scheduledTv2.setVisibility(View.GONE);
        scheduledBtn.setVisibility(View.GONE);
    }

    public void scheduledCharging() {
        immediateTv.setVisibility(View.GONE);
        scheduledTv.setVisibility(View.VISIBLE);
        scheduledTv2.setVisibility(View.VISIBLE);
        scheduledBtn.setVisibility(View.VISIBLE);
        Log.d(TAG, "scheduled");
    }

    public void smartCharging() {
        Log.d(TAG, "smart");
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
            }
        });
        return time;
    }

}
