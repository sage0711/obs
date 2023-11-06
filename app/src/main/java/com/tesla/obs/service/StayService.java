package com.tesla.obs.service;

import static com.tesla.obs.R.*;
import static com.tesla.obs.R.string.*;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


import com.tesla.obs.CacheManager;
import com.tesla.obs.DeleteDirectoryContents;
import com.tesla.obs.Helper;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.util.Objects;
import java.util.regex.*;

public class StayService extends Service {

    @Override
    public void onCreate() {
        clearMemoryCache();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Schedule the next monitoring check after a short delay
        scheduleMonitoringCheck();

        return START_STICKY;
    }

    //gets current date as string
    private String getCurrentDateString() {
        Instant instant = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            instant = Instant.now();
        }

        assert instant != null;
        return instant.toString();
    }

    //gets date integer
    //if some problem, it will return -1
    private int getCurrentDateInt(String date_str) {
        String pattern = getString(regex_extract_dateint);  // 2023-11-01T05:21:17.789Z
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(date_str);
        if(m.find()) {
            return Integer.parseInt(Objects.requireNonNull(m.group(1)));
        }
        return -1;
    }

    //gets time string as format "hh:mm:ss"
    private String getCurrentTimeString(String date_str) {
        String pattern = getString(string.regex_extract_timestr);
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(date_str);
        if(m.find()) {
            return m.group(1);
        }
        return null;
    }

    //clears memory cache
    private void clearMemoryCache() {
        Path directoryPath = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            directoryPath = Paths.get(getResources().getString(tivimate_folder_path));
        }

        CacheManager.deleteDir(new File(getResources().getString(tivimate_folder_path)));

        try {
            // Delete the contents of the directory, but not the directory itself
            DeleteDirectoryContents.emptyDirectory(directoryPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        CacheManager.clearCacheForApp(getApplicationContext(), getResources().getString(target_app));
        CacheManager.clearCacheForApp(getApplicationContext(), getResources().getString(origin_app));

    }

    //restarts target app
    private void restartTargetApp() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getResources().getString(target_app));
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(launchIntent);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), getResources().getString(failed_to_launch_brosiptv), Toast.LENGTH_LONG).show();
                stopSelf();
            }
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(unable_to_find_brosiptv_app), Toast.LENGTH_LONG).show();
            stopSelf();
        }
    }

    //checks if target app is running in real-time
    //reopens target app if it has been turned off
    //clear memory cache every 3 days
    private void scheduleMonitoringCheck() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {

            if (getCurrentDateInt(getCurrentDateString()) % 3 == 0) {
                if (getCurrentTimeString(getCurrentDateString()) == "00:00:00" || getCurrentTimeString(getCurrentDateString()) == "00:00:01") {
                    clearMemoryCache();
                    Toast.makeText(
                            getApplicationContext(),
                            getResources().getString(memory_cached),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            if (!Helper.isTargetRunning(getApplicationContext(), getResources().getString(target_app))) {
                clearMemoryCache();
                // The target app is not running, initiate restart
                restartTargetApp();
            } else {
                return;
            }
            // Schedule the next monitoring check after a short delay
            scheduleMonitoringCheck();
        }, 1000); // Delay in 1 sec
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
