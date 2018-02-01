package com.example.progressdaemon.main.account;

import android.accounts.Account;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.progressdaemon.main.Jobschuduler.JobHelper;
import com.example.progressdaemon.main.LogUtils;
import com.example.progressdaemon.main.Utils;
import com.example.progressdaemon.main.progress.LocalService;
import com.example.progressdaemon.main.progress.RemoteService;

/**
 * Created by mulinrui on 2018/2/1.
 */
public class SyncService extends Service {

    SyncAdapter syncAdapter;

    private static final String TAG = "SyncService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        syncAdapter = new SyncAdapter(getApplicationContext(), true);
    }


    static class SyncAdapter extends AbstractThreadedSyncAdapter {


        public SyncAdapter(Context context, boolean autoInitialize) {
            super(context, autoInitialize);
        }

        @Override
        public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
            //开始同步
            LogUtils.e(TAG + " 开始同步账户");
            //
            //判断一下双进程是否还在
            boolean isLocalRunning = Utils.isRunningService(getContext(), LocalService.class.getName());
            boolean isRemoteRunning = Utils.isRunningService(getContext(), RemoteService.class.getName());
            if (!isLocalRunning || !isRemoteRunning) {
                getContext().startService(new Intent(getContext(), LocalService.class));
                getContext().startService(new Intent(getContext(), RemoteService.class));
            }

            JobHelper.startJob(getContext());
        }
    }
}
