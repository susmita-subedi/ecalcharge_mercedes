package com.android.susmita.ecalcharge_mercedez_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends Activity {
    static String TAG = "MainActivity";
    @BindView(R.id.singleCarBtn)
    Button singleCarButton;
    @BindView(R.id.fleetBtn)
    Button fleetButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        singleCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent - takes us to the requested activity. Here register is the requested activity
                Intent singleCarIntent = new Intent(MainActivity.this, SingleCar.class);
                startActivity(singleCarIntent);
            }
        });
        fleetButton.setOnClickListener(v -> {
            //Intent - takes us to the requested activity. Here register is the requested activity
            Intent fleetIntent = new Intent(MainActivity.this, Fleet.class);
            startActivity(fleetIntent);
        });
    }



}
