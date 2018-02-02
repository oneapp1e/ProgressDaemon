package com.example.progressdaemon.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.progressdaemon.R;
import com.example.progressdaemon.main.Jobschuduler.JobHelper;
import com.example.progressdaemon.main.account.AccountHelper;
import com.example.progressdaemon.main.keep.KeepManager;
import com.example.progressdaemon.main.progress.LocalService;
import com.example.progressdaemon.main.progress.RemoteService;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogUtils.setDebugable(true);

        //通过一像素activity在锁屏时提权
        KeepManager.getInstance().registerKeep(this);

        //通过service 提权
//        startService(new Intent(this, ForegroundService.class));

        //通过stick拉活
//        startService(new Intent(this, StickService.class));

        //通过同步账户拉活
        AccountHelper.addAccount(this);
        AccountHelper.autoSync(this);

        //通过jobScheduler拉活
        JobHelper.startJob(this);
        //双进程拉活 里面 同时 使用service 提权  stick拉活
        startService(new Intent(this, LocalService.class));
        startService(new Intent(this, RemoteService.class));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        KeepManager.getInstance().unregisterKeep(this);
    }
}
