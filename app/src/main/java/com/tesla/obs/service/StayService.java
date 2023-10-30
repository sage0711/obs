package com.tesla.obs.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
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

public class StayService extends Service {

    private static final int NOTIFICATION_ID = 1; // You can choose any unique integer for the notification ID
    private Notification buildNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, String.valueOf(2))
                .setContentTitle("Your Notification Title")
                .setContentText("Your Notification Text")
                .setPriority(NotificationCompat.PRIORITY_LOW);

        // Create a pending intent to open the main activity when the notification is clicked
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        return builder.build();
    }



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
//        if(Helper.isTargetRunning(getApplicationContext(), getResources().getString(R.string.target_app))) {
//            Toast.makeText(getApplicationContext(), getResources().getString(R.string.already_running), Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(getApplicationContext(), getResources().getString(R.string.app_down), Toast.LENGTH_SHORT).show();
//            scheduleAppRestart();
//        }
//        return START_REDELIVER_INTENT;
        if (Helper.isTargetRunning(getApplicationContext(), getResources().getString(R.string.target_app))) {
            // The target app is running, no action needed
        } else {
            // The target app is not running, initiate restart
            restartTargetApp();
        }

        // Continue running as a foreground service
        startForeground(NOTIFICATION_ID, buildNotification());

        // Schedule the next monitoring check after a short delay
        scheduleMonitoringCheck();

        return START_STICKY;
    }



//    // Helper method to schedule the app restart after a delay
//    private void scheduleAppRestart() {
//        Handler handler = new Handler();
//        handler.postDelayed(() -> {
//            if (!Helper.isTargetRunning(getApplicationContext(), getResources().getString(R.string.target_app))) {
//                Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getResources().getString(R.string.target_app));
//                if (launchIntent != null) {
//                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    try {
//                        startActivity(launchIntent);
//                    } catch (Exception e) {
//                        Log.e("RestartError", "Failed to restart the target app: " + e.getMessage());
//                    }
//                } else {
//                    Log.e("RestartError", "Launch intent is null for the target app.");
//                }
//            }
//        }, 2000); // Delay in milliseconds (e.g., 2 seconds)
//    }

    private void restartTargetApp() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getResources().getString(R.string.target_app));
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


    private void scheduleMonitoringCheck() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (!Helper.isTargetRunning(getApplicationContext(), getResources().getString(R.string.target_app))) {
                // The target app is not running, initiate restart
                restartTargetApp();
            }
            // Schedule the next monitoring check after a short delay
            scheduleMonitoringCheck();
        }, 2000); // Delay in milliseconds (e.g., 2 seconds)
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
