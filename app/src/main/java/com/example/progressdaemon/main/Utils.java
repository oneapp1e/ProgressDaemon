package com.example.progressdaemon.main;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by mulinrui on 2018/2/1.
 */
public class Utils {

    public static boolean isRunningService(Context context, String serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info : runningServices) {
            if (TextUtils.equals(info.service.getClassName(), serviceName)) {
                return true;
            }
        }
        return false;
    }
}
