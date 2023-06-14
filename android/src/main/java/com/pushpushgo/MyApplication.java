package com.pushpushgo;

import android.app.Application;

import com.pushpushgo.sdk.PushPushGo;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PushPushGo.getInstance(this);
    }
}
