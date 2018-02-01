package com.example.progressdaemon.main.Jobschuduler;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;

import com.example.progressdaemon.main.LogUtils;
import com.example.progressdaemon.main.Utils;
import com.example.progressdaemon.main.progress.LocalService;
import com.example.progressdaemon.main.progress.RemoteService;

/**
 * Created by mulinrui on 2018/2/1.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MyJobService extends JobService {

    private static final String TAG = "MyJobService";

    @Override
    public boolean onStartJob(JobParameters params) {
        LogUtils.e(TAG + " 开启job");
        //7.0以上 轮询
        if (Build.VERSION.SDK_INT >= 24) {
            JobHelper.startJob(this);
        }

        //判断一下双进程是否还在
        boolean isLocalRunning = Utils.isRunningService(this, LocalService.class.getName());
        boolean isRemoteRunning = Utils.isRunningService(this, RemoteService.class.getName());
        if (!isLocalRunning || !isRemoteRunning) {
            startService(new Intent(this, LocalService.class));
            startService(new Intent(this, RemoteService.class));
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        LogUtils.e(TAG + " job 停止了");
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e(TAG + " job onDestroy");
    }
}
