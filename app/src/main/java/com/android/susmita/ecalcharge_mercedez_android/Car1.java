package com.android.susmita.ecalcharge_mercedez_android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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

/**
 * Created by susmita on 9/21/2017.
 */

public class Car1  {
    String TAG = "car1";
    byte[] vehicleSerial;
    @BindView(R.id.car1soc)
    TextView car1soc;
    @BindView(R.id.car2soc)
    TextView car2soc;
    @BindView(R.id.car3soc)
    TextView car3soc;
    private Context context;
    public Car1(Context context) {
        this.context = context;
        getInstance();
        downloadCertificate();
        findSoc();
    }
    public void getInstance() {
        Manager.getInstance().initialize(
                "dGVzdDzJFTy4zTnBNmCoyXGqO65YY+4JFpdYA0aJvRB4zyjrOHTLFb3VB2SLvvOStdRi0hXEjv5dJ1zBZXne7HEEFB/hnHaTFMTB/sdm5yoQREJgdt9osCwgLiwWdsmQ0f2G5MgYK8URdbxiAiZNVK14G28BCB0ITmsIkZAEmcbCRGZeN2aLBMc8FoPxvPdpEybJKaXSCGnd",
                "bMrg2kovov8xOKpC7HAsxAWx4fxcPyysj+9Y6XvOGZE=",
                "mtgEqamle56rOE2oKVyh3IJ/5hmOhEyhT/I8yEuJ+MWAUhWR4qYR6TLPQTxJ1amdgApRLCXL/RJyaA0PDJaxEw==",context
        );
        Log.d(TAG, "instance granted: ");
    }
    private void downloadCertificate() {
//        Manager.getInstance().downloadCertificate("f-EyOjLDPTiiKGNiW4cMr1wJu73ecDX7zysapWWMpYuvJRLJ3g1HhRM9vkkswBW2BEmLjaH7nLlenMUOqnJsuzwZ_lOSbEy5YdFbB0NJTwPacrLJIwV0sXCm_1I8juDG7Q",

//                new Manager.DownloadCallback() {
        Manager.getInstance().downloadCertificate("f-EyOjLDPTiiKGNiW4cMr1wJu73ecDX7zysapWWMpYuvJRLJ3g1HhRM9vkkswBW2BEmLjaH7nLlenMUOqnJsuzwZ_lOSbEy5YdFbB0NJTwPacrLJIwV0sXCm_1I8juDG7Q", new Manager.DownloadCallback() {
            @Override
            public void onDownloaded(byte[] bytes) {
                Log.d(TAG, "Access granted: ");
                vehicleSerial = bytes;
            }

            @Override
            public void onDownloadFailed(DownloadAccessCertificateError downloadAccessCertificateError) {
                Log.d(TAG, "onDownloadFailed " + downloadAccessCertificateError.getType() + downloadAccessCertificateError.getMessage());
            }
        });
    }
    public void findSoc() {
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
                        car1soc.setText(String.valueOf(state.getBatteryLevel() * 100));
                    }
                } catch (CommandParseException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }

            @Override
            public void onCommandFailed(TelematicsError telematicsError) {
            }
        });

    }
}
