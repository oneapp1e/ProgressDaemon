package com.example.progressdaemon.main.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.progressdaemon.main.LogUtils;

/**
 * Created by mulinrui on 2018/2/1.
 */
public class ForegroundService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //让服务变成前台服务
        LogUtils.e(" ForegroundService 服务提权已经启动 ");
        startForeground(10, new Notification());
        //如果是18以上的设备  这样操作会在通知栏有一个通知，那再开启个服务把通知干掉
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            startService(new Intent(getApplicationContext(), InnnerService.class));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e(" ForegroundService 服务提权已经销毁 ");
    }

}
