package com.example.progressdaemon;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.widget.Toast;


/**
 * Created by mulinrui on 2016/9/8.
 */
public class LocalService extends Service {

    private MyBind mMyBind;
    private MyConn mMyConn;
    // ==========================================================================
    // Constants
    // ==========================================================================

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMyBind;
    }


    // ==========================================================================
    // Fields
    // ==========================================================================

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        mMyBind = new MyBind();
        if (mMyConn == null) {
            mMyConn = new MyConn();
        }
        LocalService.this.bindService(new Intent(LocalService.this, RemoteService.class), mMyConn, Context.BIND_IMPORTANT);
    }

    // ==========================================================================
    // Constructors
    // ==========================================================================


    // ==========================================================================
    // Getters
    // ==========================================================================


    // ==========================================================================
    // Setters
    // ==========================================================================

    // ==========================================================================
    // Methods
    // ==========================================================================


    // ==========================================================================
    // Inner/Nested Classes
    // ==========================================================================
    class MyBind extends IProgressService.Stub {

        @Override
        public String getServiceName() throws RemoteException {
            return "LocalService";
        }
    }

    class MyConn implements ServiceConnection {


        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.e("testbbs 连接远程服务开始");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.e("testbbs 远程服务被杀死了");
            Toast.makeText(LocalService.this, "远程服务被杀死了!", Toast.LENGTH_SHORT).show();
            //启动远程服务  绑定远程服务
            LocalService.this.startService(new Intent(LocalService.this, RemoteService.class));
            if (mMyConn == null) {
                mMyConn = new MyConn();
            }
            LocalService.this.bindService(new Intent(LocalService.this, RemoteService.class), mMyConn, Context.BIND_IMPORTANT);
        }
    }

}
