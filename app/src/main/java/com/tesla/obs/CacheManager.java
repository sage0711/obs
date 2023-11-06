package com.tesla.obs;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.File;

public class CacheManager {

    // Clear the cache for a specific app
    public static void clearCacheForApp(Context context, String packageName) {
        try {
            // Get the package manager
            PackageManager pm = context.getPackageManager();

            // Get the data directory for the app
            String dataDir = null;
            try {
                dataDir = pm.getApplicationInfo(packageName, 0).dataDir;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (dataDir != null) {
                // Clear the cache directory
                File cacheDir = new File(dataDir, "cache");
                if (cacheDir.exists()) {
                    deleteDir(cacheDir);
                }

                // Clear the external cache directory
                File externalCacheDir = context.getExternalCacheDir();
                if (externalCacheDir != null && externalCacheDir.exists()) {
                    deleteDir(externalCacheDir);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Recursively delete a directory and its contents
    public static void deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                File childFile = new File(dir, child);
                if (childFile.isDirectory()) {
                    deleteDir(childFile);
                } else {
                    childFile.delete();
                }
            }
        }
        dir.delete();
    }
}
