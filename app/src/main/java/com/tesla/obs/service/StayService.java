package com.tesla.obs.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.tesla.obs.Helper;
import com.tesla.obs.R;

import androidx.annotation.Nullable;

public class StayService extends Service {

    @Override
    public void onCreate() {
        Log.d("OnCreateLog", "StayService.onCreate executed");
        if(Helper.isTargetRunning(getApplicationContext(), getResources().getString(R.string.target_app))) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.already_running), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.app_down), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(Helper.isTargetRunning(getApplicationContext(), getResources().getString(R.string.target_app))) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.already_running), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.app_down), Toast.LENGTH_SHORT).show();
            while(true) {
                if(Helper.isTargetRunning(getApplicationContext(), getResources().getString(R.string.target_app))) {
                    return START_STICKY;
                }
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getResources().getString(R.string.target_app));
                if (launchIntent != null) {
                    startActivity(launchIntent);
                }
            }
        }
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
