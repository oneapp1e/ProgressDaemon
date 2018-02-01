package com.example.progressdaemon.main.keep;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.example.progressdaemon.main.LogUtils;

/**
 * Created by mulinrui on 2018/2/1.
 */
public class KeepReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtils.e("接收到的广播：" + action);
        if (TextUtils.equals(action, Intent.ACTION_SCREEN_OFF)) {
            //锁屏 开启activity
            KeepManager.getInstance().startKeep(context);
        } else if (TextUtils.equals(action, Intent.ACTION_SCREEN_ON)) {
            KeepManager.getInstance().finishKeep();
        }
    }
}
