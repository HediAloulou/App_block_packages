package com.example.prediction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppSelectionActivity extends AppCompatActivity {
    private LinearLayout appListLayout;
    private SharedPreferences sharedPreferences;
    private Set<String> selectedApps;

    private static final String TAG = "AppSelectionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selection);

        appListLayout = findViewById(R.id.appListLayout);
        sharedPreferences = getSharedPreferences("com.example.prediction", Context.MODE_PRIVATE);
        selectedApps = sharedPreferences.getStringSet("blockedAppsForKids", new HashSet<>());

        PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo appInfo : apps) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(appInfo.loadLabel(packageManager));
            checkBox.setChecked(selectedApps.contains(appInfo.packageName));
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedApps.add(appInfo.packageName);
                    Log.d(TAG, "App selected: " + appInfo.packageName); // Log when app is selected
                } else {
                    selectedApps.remove(appInfo.packageName);
                    Log.d(TAG, "App deselected: " + appInfo.packageName); // Log when app is deselected
                }
            });
            appListLayout.addView(checkBox);
        }

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            sharedPreferences.edit().putStringSet("blockedAppsForKids", selectedApps).apply();
            Log.d(TAG, "Blocked apps saved: " + selectedApps); // Log when blocked apps are saved
            finish();
        });
    }
}
