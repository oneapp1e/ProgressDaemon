package com.example.progressdaemon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogUtils.setDebugable(true);

        startService(new Intent(this,LocalService.class));
        startService(new Intent(this,RemoteService.class));
    }
}
