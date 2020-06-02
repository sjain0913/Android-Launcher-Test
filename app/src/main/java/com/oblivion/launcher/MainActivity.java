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
import android.widget.ImageView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView calculatorIcon = (ImageView) findViewById(R.id.calculatorButton);
        calculatorIcon.setImageDrawable(getActivityIcon(this, "com.sec.android.app.popupcalculator", "com.sec.android.app.popupcalculator.Calculator"));

        ImageView calendarIcon = (ImageView) findViewById(R.id.calendarButton);
        calendarIcon.setImageDrawable(getActivityIcon(this, "com.samsung.android.calendar", "com.android.calendar.AllInOneActivity"));
    }



    public void onCalculatorButtonClick(View v) {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.sec.android.app.popupcalculator");
        startActivity(launchIntent);
    }

    public void onCalendarButtonClick(View v) {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.samsung.android.calendar");
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

    // some errors - most likely wont need this because we have the same 2 apps always
    // Method to get package name for a certain appname (to pick the 2 apps to display in launcher)
//    protected String getPackageName(String AppName) {
//        List<PackageInfo> packages = getPackageManager().getInstalledPackages(PackageManager.GET_META_DATA);
//        String name = "";
//        for (PackageInfo packageInfo: packages) {
//            if (packageInfo.applicationInfo.name != null) {
//                if (packageInfo.applicationInfo.name.contains(AppName) || AppName.contains((packageInfo.applicationInfo.name))) {
//                    name = packageInfo.packageName;
//                } else {
//                    name = "Not Found";
//                }
//            }
//        }
//        return name;
//    }

    // some errors - most likely wont need this because we have the same 2 apps always
    // Instead, use Package Viewer App
//    protected String getActivityName(String PackageName) {
//        List<PackageInfo> packages = getPackageManager().getInstalledPackages(PackageManager.GET_META_DATA);
//        String name = "";
//        for (PackageInfo packageInfo : packages) {
//            if (PackageName.contains(packageInfo.packageName) || packageInfo.packageName.contains(PackageName)) {
//                String activityName = packageInfo.
//            }
//            return name;
//        }
//    }

}
