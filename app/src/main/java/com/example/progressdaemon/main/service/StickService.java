package com.example.progressdaemon.main.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.progressdaemon.main.LogUtils;

/**
 * 通过粘性拉活服务，但是对于国内的厂商  大部分都无作用
 * Created by mulinrui on 2018/2/1.
 */
public class StickService extends Service {

    private static final String TAG = "StickService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e(TAG + "  服务已经启动 ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.e(TAG + "  服务onStartCommand 触发拉活 ");
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e(TAG + "  服务已经启动 ");
    }
}
