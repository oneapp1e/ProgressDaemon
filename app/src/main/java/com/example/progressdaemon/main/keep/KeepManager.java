package com.example.progressdaemon.main.keep;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.lang.ref.WeakReference;

/**
 * Created by mulinrui on 2018/2/1.
 */
public class KeepManager {

    //单例 饿汉式
    private static final KeepManager instance = new KeepManager();

    private WeakReference<Activity> mKeepAct;

    public static KeepManager getInstance() {
        return instance;
    }

    private KeepManager() {

    }

    private KeepReceiver keepReceiver;


    /**
     * 注册 开屏 锁屏广播
     */
    public void registerKeep(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        keepReceiver = new KeepReceiver();
        context.registerReceiver(keepReceiver, filter);
    }

    public void unregisterKeep(Context context) {
        if (keepReceiver != null) {
            context.unregisterReceiver(keepReceiver);
        }
    }

    /**
     * 启动一像素activity
     */
    public void startKeep(Context context) {
        Intent intent = new Intent(context, KeepActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void finishKeep() {
        if (mKeepAct != null) {
            Activity activity = mKeepAct.get();
            if (activity != null) {
                activity.finish();
            }
            mKeepAct = null;
        }
    }

    /**
     * 将启动的activity放到弱引用中
     */
    public void setKeep(KeepActivity keep) {
        mKeepAct = new WeakReference<Activity>(keep);
    }

}
