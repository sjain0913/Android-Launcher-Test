package com.oblivion.launcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



    public void onCalculatorButtonClick(View v) {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName("Calculator"));
        startActivity(launchIntent);
    }


    // Method to get app icon for an app (for the 2 apps in launcher)
    public static Drawable getActivityIcon(Context context, String packageName, String activityName) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, activityName));
        ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);

        return resolveInfo.loadIcon(pm);
    }

    // Methpd to get package name for a certain appname (to pick the 2 apps to display in launcher)
    protected String getPackageName(String AppName) {
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(PackageManager.GET_META_DATA);
        String name = "";
        for (PackageInfo packageInfo: packages) {
            if (packageInfo.applicationInfo.name.contains(AppName) || AppName.contains((packageInfo.applicationInfo.name))) {
                name = packageInfo.packageName;
            } else {
                name = "Not Found";
            }
        }
        return name;
    }


}
