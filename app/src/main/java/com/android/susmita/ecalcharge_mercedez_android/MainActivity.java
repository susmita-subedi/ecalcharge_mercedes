package com.android.susmita.ecalcharge_mercedez_android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.highmobility.hmkit.Broadcaster;
import com.highmobility.hmkit.BroadcasterListener;
import com.highmobility.hmkit.Command.Command;
import com.highmobility.hmkit.Command.CommandParseException;
import com.highmobility.hmkit.Command.Incoming.ChargeState;
import com.highmobility.hmkit.Command.Incoming.IncomingCommand;
import com.highmobility.hmkit.ConnectedLink;
import com.highmobility.hmkit.ConnectedLinkListener;
import com.highmobility.hmkit.Error.BroadcastError;
import com.highmobility.hmkit.Error.DownloadAccessCertificateError;
import com.highmobility.hmkit.Error.TelematicsError;
import com.highmobility.hmkit.Link;
import com.highmobility.hmkit.Manager;
import com.highmobility.hmkit.Telematics;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends Activity implements BroadcasterListener, ConnectedLinkListener {
    static String TAG = "MainActivity";
    Broadcaster broadcaster;
    ConnectedLink link;
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
    private RadioGroup radioGroup;
    private float time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getInstance();
        broadcaster = Manager.getInstance().getBroadcaster();
        broadcaster.startBroadcasting(new Broadcaster.StartCallback() {
            @Override
            public void onBroadcastingStarted() {
                Log.d(TAG, "onBroadcastingStarted: ");
            }

            @Override
            public void onBroadcastingFailed(BroadcastError broadcastError) {
                Log.d(TAG, "onBroadcastingStarted: " + broadcastError.getType() + broadcastError.getMessage());
            }

        });
        broadcaster.setListener(this);
        downloadCertificate();
        chargeButton.setOnClickListener(view -> startCharging());
        socButton.setOnClickListener(view -> findSoc());
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.clearCheck();
        radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {
            selectChargingOptions(radioGroup1, i);
        });
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

    @Override
    public void onStateChanged(Broadcaster.State state) {
        Log.d(TAG, "onStateChanged: " + state);
    }

    @Override
    public void onLinkReceived(ConnectedLink connectedLink) {
        link = connectedLink;
        link.setListener(this);
    }

    @Override
    public void onLinkLost(ConnectedLink connectedLink) {
        link.setListener(null);

    }

    @Override
    public void onAuthorizationRequested(ConnectedLink connectedLink, AuthorizationCallback authorizationCallback) {
        authorizationCallback.approve();
    }

    @Override
    public void onAuthorizationTimeout(ConnectedLink connectedLink) {
    }

    @Override
    public void onStateChanged(Link link, Link.State state) {
        if (link.getState() == Link.State.AUTHENTICATED) {
        }
    }

    @Override
    public void onCommandReceived(Link link, byte[] bytes) {
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
        }
        //                byte[] command = Command.Charging.getChargeState();
        else {
            command = Command.Charging.startCharging(true);
        }
//        command = Command.Charging.startCharging(true);
        telematics.sendCommand(command, vehicleSerial, new Telematics.CommandCallback() {
            @Override
            public void onCommandResponse(byte[] bytes) {
                try {
                    IncomingCommand incomingCommand = IncomingCommand.create(bytes);
                    ChargeState chargeState = (ChargeState) incomingCommand;
//                    socTextView.setText(String.valueOf(chargeState.getChargingState()));
                    if ((String.valueOf(chargeState.getChargingState())).equals("DISCONNECTED")) {
                        charging = false;
                        chargeButton.setText("Turn On");

                    } else {
                        charging = true;
                        chargeButton.setText("Turn Off");
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
        switch (index) {
            case 0: {
                immediateCharging();
                break;
            }
            case 1: {
                smartCharging();
                break;
            }
            case 2: {
                scheduledCharging();
                break;
            }
        }
    }

    public void immediateCharging() {
//        socTextView.setText("0");
        startCharging();
//        float timeToCharge = findEstimatedChargingTime();
//        socTextView.setText(String.valueOf(timeToCharge));
        float timeToCharge = findSoc();
//        timeTextView.setText(String.valueOf(timeToCharge));
    }

    public void smartCharging() {
        socTextView.setText("1");
    }

    public void scheduledCharging() {
        socTextView.setText("2");
    }

    public float findEstimatedChargingTime() {
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
                        socTextView.setText(String.valueOf(time));
                    }
                } catch (CommandParseException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                    Log.d(TAG, "SOrry");
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
