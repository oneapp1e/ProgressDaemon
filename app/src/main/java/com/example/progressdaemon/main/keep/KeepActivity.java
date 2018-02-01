package com.example.progressdaemon.main.keep;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.example.progressdaemon.main.LogUtils;
import com.example.progressdaemon.main.Utils;
import com.example.progressdaemon.main.progress.LocalService;
import com.example.progressdaemon.main.progress.RemoteService;

/**
 * 使用一像素activity进行提权  仅能用于锁屏
 * Created by mulinrui on 2018/2/1.
 */
public class KeepActivity extends Activity {

    private static final String TAG = "KeepActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtils.e(TAG + " 启动 keep");

        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        //宽高设置1
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = 1;
        attributes.height = 1;
        attributes.x = 0;
        attributes.y = 0;
        window.setAttributes(attributes);

        KeepManager.getInstance().setKeep(this);

        //判断一下双进程是否还在
        boolean isLocalRunning = Utils.isRunningService(this, LocalService.class.getName());
        boolean isRemoteRunning = Utils.isRunningService(this, RemoteService.class.getName());
        if (!isLocalRunning || !isRemoteRunning) {
            startService(new Intent(this, LocalService.class));
            startService(new Intent(this, RemoteService.class));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.e(TAG + " 关闭 keep");
    }
}
