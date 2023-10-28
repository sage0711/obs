package com.tesla.obs;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.tesla.obs.Helper;

import androidx.annotation.Nullable;

public class StayService extends Service {

    @Override
    public void onCreate() {
        if(Helper.isTargetRunning(this, String.valueOf(R.string.target_app))) {
            Toast.makeText(this, String.valueOf(R.string.already_running), Toast.LENGTH_LONG);
        } else {
            Toast.makeText(this, String.valueOf(R.string.app_down), Toast.LENGTH_LONG);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public void onDestroy() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
