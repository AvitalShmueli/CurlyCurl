package com.example.curlycurl;

import android.app.Application;

import com.example.curlycurl.Utilities.SignalManager;

public class App extends Application {

    public static final double DEFAULT_LAN = 32.113442;
    public static final double DEFAULT_LON = 34.818028;

    @Override
    public void onCreate() {
        super.onCreate();
        SignalManager.init(this);
    }
}
