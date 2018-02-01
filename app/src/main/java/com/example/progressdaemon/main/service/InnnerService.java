package com.example.progressdaemon.main.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by mulinrui on 2018/2/1.
 */
public class InnnerService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //使用同一个通知id
        startForeground(10, new Notification());
        stopSelf();
    }
}
