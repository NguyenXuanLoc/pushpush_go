package com.pushpushgo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pushpushgo.sdk.PushPushGo;

import io.flutter.embedding.android.FlutterActivity;

public class MainPushPushGoActivity  extends FlutterActivity {
    MyApplication myApplication;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null && getIntent() != null) {
            PushPushGo.getInstance().handleBackgroundNotificationClick(getIntent());
        }
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        PushPushGo.getInstance().handleBackgroundNotificationClick(intent);
    }
}
