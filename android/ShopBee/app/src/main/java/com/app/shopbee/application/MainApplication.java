package com.app.shopbee.application;

/**
 * Created by sendilkumar on 06/06/15.
 */

import android.app.Application;
import android.content.Context;

public class MainApplication extends Application {

    private static MainApplication sInstance;

    public static MainApplication getsInstance() {
        return sInstance;
    }

    public static void setsInstance(MainApplication sInstance) {
        MainApplication.sInstance = sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }


}

