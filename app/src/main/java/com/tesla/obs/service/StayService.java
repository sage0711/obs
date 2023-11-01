package com.tesla.obs.service;

import static com.tesla.obs.R.*;
import static com.tesla.obs.R.string.*;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


import com.tesla.obs.CacheManager;
import com.tesla.obs.Helper;
import com.tesla.obs.MainActivity;
import com.tesla.obs.R;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.time.*;
import java.util.Date;
import java.util.regex.*;

public class StayService extends Service {



    @Override
    public void onCreate() {
        Log.d("OnCreateLog", "StayService.onCreate executed");
        if(Helper.isTargetRunning(getApplicationContext(), getResources().getString(target_app))) {
            Toast.makeText(getApplicationContext(), getResources().getString(already_running), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(app_down), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Helper.isTargetRunning(getApplicationContext(), getResources().getString(target_app))) {
            // The target app is running, no action needed
        } else {
            // The target app is not running, initiate restart
            restartTargetApp();
        }

        // Schedule the next monitoring check after a short delay
        scheduleMonitoringCheck();

        return START_STICKY;
    }

    private void restartTargetApp() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getResources().getString(target_app));
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(launchIntent);
            } catch (Exception e) {
                Log.e("RestartError", "Failed to restart the target app: " + e.getMessage());
            }
        } else {
            Log.e("RestartError", "Launch intent is null for the target app.");
        }
    }

    private String getCurrentDateString() {
        Instant instant = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            instant = Instant.now();
        }

        return instant.toString();
    }

    private int getCurrentDateInt(String date_str) {
        String pattern = "\\d{4}-\\d{2}-(\\d{2})T\\d{2}:\\d{2}:\\d{2}.\\d+Z";  // 2023-11-01T05:21:17.789Z
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(date_str);
        if(m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return -1;
    }

    private String getCurrentTimeString(String date_str) {
        String pattern = "\\d{4}-\\d{2}-\\d{2}T(\\d{2}:\\d{2}:\\d{2}).\\d+Z";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(date_str);
        if(m.find()) {
            return m.group(1);
        }
        return null;
    }

    private void scheduleMonitoringCheck() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {

            if (getCurrentDateInt(getCurrentDateString()) % 3 == 0) {
                if (getCurrentTimeString(getCurrentDateString()) == "00:00:00" || getCurrentTimeString(getCurrentDateString()) == "00:00:01") {
                    CacheManager.clearCacheForApp(getApplicationContext(), getResources().getString(target_app));
                    CacheManager.clearCacheForApp(getApplicationContext(), getResources().getString(origin_app));
                    Toast.makeText(
                            getApplicationContext(),
                            getResources().getString(memory_cached),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            if (!Helper.isTargetRunning(getApplicationContext(), getResources().getString(target_app))) {
                CacheManager.clearCacheForApp(getApplicationContext(), getResources().getString(target_app));
                CacheManager.clearCacheForApp(getApplicationContext(), getResources().getString(origin_app));
                // The target app is not running, initiate restart
                restartTargetApp();
            }
            // Schedule the next monitoring check after a short delay
            scheduleMonitoringCheck();
        }, 1000); // Delay in milliseconds (e.g., 2 seconds)
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
