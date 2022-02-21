package com.celex.rider.AllActivitys;

import android.app.Application;

import com.roam.sdk.Roam;

public class BaseApplication extends Application {

    @Override

    public void onCreate() {
        super.onCreate();
        Roam.initialize(this, "2f987f52e3750c76ff835ed383ca4e8d8d9138b074372d04efe681c9cc63deba");
    }

}

