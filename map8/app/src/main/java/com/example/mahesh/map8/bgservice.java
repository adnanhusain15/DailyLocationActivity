package com.example.mahesh.map8;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class bgservice extends IntentService {
    public static Context tt=null;
    public bgservice() {
        super("");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Service Started", Toast.LENGTH_SHORT).show();
            }
        });
        while (MainActivity.sstatus) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Service Stopped", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
