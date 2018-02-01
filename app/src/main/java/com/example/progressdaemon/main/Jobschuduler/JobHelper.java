package com.example.progressdaemon.main.Jobschuduler;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

/**
 * Created by mulinrui on 2018/2/1.
 */
public class JobHelper {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void startJob(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            JobInfo.Builder builder = new JobInfo.Builder(10, new ComponentName(context.getPackageName(),
                    MyJobService.class.getName())).setPersisted(true);

            //小于7.0
            if (Build.VERSION.SDK_INT < 24) {
                builder.setPeriodic(1_000);
            } else {
                builder.setMinimumLatency(1_000);
            }
            jobScheduler.schedule(builder.build());
        }


    }
}
