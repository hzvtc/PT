package com.as.pt.application;

import android.app.Application;

import com.as.pt.util.PalLog;

/**
 * Created by FJQ on 2017/2/8.
 */
//@Logger(tag = "StandardApp", level = Logger.INFO)
public class StandardApp extends Application {

    private static final String TAG="StandardApp";
    @Override
    public void onCreate() {
        super.onCreate();
        PalLog.init(getApplicationContext());
    }
}
