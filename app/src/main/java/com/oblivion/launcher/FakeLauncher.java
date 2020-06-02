package com.oblivion.launcher;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class FakeLauncher extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // function to bring up the fake launcher so default list gets cleared, prompts user to select default.
    public static void makePreferred(Context c) {
        PackageManager pm = c.getPackageManager();
        ComponentName cn = new ComponentName(c, FakeLauncher.class);
        pm.setComponentEnabledSetting(cn, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        Intent selector = new Intent(Intent.ACTION_MAIN);
        selector.addCategory(Intent.CATEGORY_HOME);
        c.startActivity(selector);
        pm.setComponentEnabledSetting(cn, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    //    boolean isDefault() {
//        boolean def = false;
//        final IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
//        filter.addCategory(Intent.CATEGORY_HOME);
//
//        List<IntentFilter> filters = new ArrayList<IntentFilter>();
//        filters.add(filter);
//
//        final String myPackageName = getPackageName();
//        List<ComponentName> activities = new ArrayList<ComponentName>();
//        final PackageManager packageManager = (PackageManager) getPackageManager();
//
//        // You can use name of your package here as third argument
//        packageManager.getPreferredActivities(filters, activities, "com.oblivion.launcher");
//        List<String> names = new ArrayList<>();
//        for (ComponentName activity : activities) {
//            if (myPackageName.equals(activity.getPackageName())) {
//                def = true;
//            }
//            names.add(activity.getPackageName());
//        }
//        if names.contains("com.oblivion.launcher")
//
//        return false;
//    }
}
