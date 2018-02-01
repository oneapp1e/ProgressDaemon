package com.example.progressdaemon.main.progress;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.progressdaemon.IProgressService;
import com.example.progressdaemon.main.LogUtils;


/**
 * Created by mulinrui on 2016/9/8.
 */
public class RemoteService extends Service {

    private MyBind mMyBind;
    private MyConn mMyConn;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMyBind;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.setDebugable(true);
        LogUtils.e("testbbs 远程服务启动了");

        mMyBind = new MyBind();
        if (mMyConn == null) {
            mMyConn = new MyConn();
        }
        startForeground(20, new Notification());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            startService(new Intent(this, InnerService.class));
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bindService(new Intent(RemoteService.this, LocalService.class), mMyConn,
                BIND_IMPORTANT);
        return super.onStartCommand(intent, flags, startId);
    }


    class MyBind extends IProgressService.Stub {

        @Override
        public String getServiceName() throws RemoteException {
            return "RemoteService";
        }
    }

    class MyConn implements ServiceConnection {


        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.e("testbbs 连接本地服务开始");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(RemoteService.this, "本地服务被杀死了!", Toast.LENGTH_SHORT).show();
            //启动远程服务  绑定远程服务
            RemoteService.this.startService(new Intent(RemoteService.this, LocalService.class));
            if (mMyConn == null) {
                mMyConn = new MyConn();
            }
            RemoteService.this.bindService(new Intent(RemoteService.this, LocalService.class), mMyConn, Context.BIND_IMPORTANT);
        }
    }

    public static class InnerService extends Service {


        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            startForeground(20, new Notification());
            stopSelf();
        }
    }
}
