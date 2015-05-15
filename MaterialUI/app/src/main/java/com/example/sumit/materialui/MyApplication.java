package com.example.sumit.materialui;

import android.app.Application;
import android.content.Context;

/**
 * Created by sumit on 08/05/2015.
 */
public class MyApplication extends Application{
    private static MyApplication sInstance;
    public static final String API_KEY_ROTTEN_TOMATOES="vbqtp9w9hpzgmnegvtxbr5bj";

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static MyApplication getsInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }
}
