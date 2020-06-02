package com.oblivion.launcher;

import androidx.appcompat.app.AppCompatActivity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public android.widget.Button Button;

    private boolean IsKioskEnabled = false;
    private static final String password = "123456789";
    private static final String APP1_PACKAGE = "com.oblivion.test1";
    private static final String APP2_PACKAGE = "com.oblivion.test2";
    private static final String[] APP_PACKAGES = {APP1_PACKAGE, APP2_PACKAGE, "com.oblivion.launcher"};
    private View decorView;
    private DevicePolicyManager dpm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        decorView = getWindow().getDecorView();

        // Setting Icon Positions
        ImageView test1Icon = (ImageView) findViewById(R.id.test1Button);
        test1Icon.setImageDrawable(getActivityIcon(this, "com.oblivion.test1", "com.oblivion.test1.MainActivity"));
        test1Icon.setX(50);
        test1Icon.setY(50);

        ImageView test2Icon = (ImageView) findViewById(R.id.test2Button);
        test2Icon.setImageDrawable(getActivityIcon(this, "com.oblivion.test2", "com.oblivion.test2.MainActivity"));
        test2Icon.setX(250);
        test2Icon.setY(50);

        // Warns if app is not admin or sets whitelist if everything is fine
        ComponentName deviceAdmin = new ComponentName(this, AdminReceiver.class);
        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (!dpm.isAdminActive(deviceAdmin)) {
            Toast.makeText(this, getString(R.string.not_device_admin), Toast.LENGTH_SHORT).show();
        }
        if (dpm.isDeviceOwnerApp(getPackageName())) {
            dpm.setLockTaskPackages(deviceAdmin, APP_PACKAGES);
        } else {
            Toast.makeText(this, getString(R.string.not_device_owner), Toast.LENGTH_SHORT).show();
        }
    }



    public void onTest1ButtonClick(View v) {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.oblivion.test1");
        startActivity(launchIntent);
    }

    public void onTest2ButtonClick(View v) {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.oblivion.test2");
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

//    @Override
//    protected void onResume() {
//        super.onResume();
//        hideSystemUI();
//    }

    // Used to hide System UI once kiosk mode enabled
    private void hideSystemUI() {
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void enableKioskMode(boolean enabled) {
        try {
            if (enabled) {
                if (dpm.isLockTaskPermitted(this.getPackageName())) {
                    startLockTask();
                    IsKioskEnabled = true;
                    Button.setText(getString(R.string.exit_kiosk_mode));
                } else {
                    Toast.makeText(this, getString(R.string.kiosk_not_permitted), Toast.LENGTH_SHORT).show();
                }
            } else {
                stopLockTask();
                IsKioskEnabled = false;
                Button.setText(getString(R.string.enter_kiosk_mode));
            }
        } catch (Exception e) {

        }
    }

}
