package com.android.susmita.ecalcharge_mercedez_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by susmita on 9/20/2017.
 */

public class Fleet extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fleet);
        Car1 car1 = new Car1(getApplicationContext());
    }

}
