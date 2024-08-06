package com.example.prediction;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

public class AppBlockerAccessibilityService extends AccessibilityService {
    private static final String TAG = "AppBlockerService";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            String packageName = event.getPackageName() != null ? event.getPackageName().toString() : "";
            SharedPreferences sharedPreferences = getSharedPreferences("com.example.prediction", MODE_PRIVATE);
            boolean isKid = sharedPreferences.getBoolean("isKid", false);
            Set<String> blockedAppsForKids = sharedPreferences.getStringSet("blockedAppsForKids", new HashSet<>());

            Log.d(TAG, "Window state changed: " + packageName + ", isKid: " + isKid);

            if (isKid && blockedAppsForKids.contains(packageName)) {
                Toast.makeText(this, "This app is blocked for kids.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Blocking app: " + packageName);
                performGlobalAction(GLOBAL_ACTION_BACK);
            }
        }
    }

    @Override
    public void onInterrupt() {
        // Required method but can be left empty
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "Service connected");

        SharedPreferences sharedPreferences = getSharedPreferences("com.example.prediction", MODE_PRIVATE);
        Set<String> blockedAppsForKids = sharedPreferences.getStringSet("blockedAppsForKids", new HashSet<>());

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.packageNames = blockedAppsForKids.toArray(new String[0]);
        info.notificationTimeout = 100;
        setServiceInfo(info);
    }
}
