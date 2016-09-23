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
public class RemoteService extends Service {

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
        RemoteService.this.bindService(new Intent(RemoteService.this, LocalService.class), mMyConn, Context.BIND_IMPORTANT);
    }

    // ==========================================================================
    // Fields
    // ==========================================================================


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
}
