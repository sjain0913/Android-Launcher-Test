package com.oblivion.launcher;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final Context context = this;
    private EditText result;
    private static final String password = "abc";
    private static final String APP1_PACKAGE = "com.oblivion.test1";
    private static final String APP2_PACKAGE = "com.oblivion.test2";
    private static final String[] APP_PACKAGES = {APP1_PACKAGE, APP2_PACKAGE, "com.oblivion.launcher"};

    final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().getDecorView().setSystemUiVisibility(flags);
        setContentView(R.layout.activity_main);

        // Setting Icon Positions
        ImageView test1Icon = (ImageView) findViewById(R.id.test1Button);
        test1Icon.setImageDrawable(getActivityIcon(this, "com.oblivion.test1", "com.oblivion.test1.MainActivity"));
        test1Icon.setX(50);
        test1Icon.setY(50);

        ImageView test2Icon = (ImageView) findViewById(R.id.test2Button);
        test2Icon.setImageDrawable(getActivityIcon(this, "com.oblivion.test2", "com.oblivion.test2.MainActivity"));
        test2Icon.setX(250);
        test2Icon.setY(50);

        DevicePolicyManager myDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName mDPM = new ComponentName(this, AdminReceiver.class);

        if (myDevicePolicyManager.isDeviceOwnerApp(this.getPackageName())) {

            myDevicePolicyManager.setLockTaskPackages(mDPM, APP_PACKAGES);
            startLockTask();
        } else {
            Toast.makeText(getApplicationContext(),"Not owner", Toast.LENGTH_LONG).show();
        }

        Button lock_btn = (Button)findViewById(com.oblivion.launcher.R.id.lock_button);
        lock_btn.setX(50);
        lock_btn.setY(400);

        Button unlock_btn = (Button)findViewById(com.oblivion.launcher.R.id.unlock_button);
        unlock_btn.setX(250);
        unlock_btn.setY(400);

        Button remove_admin = (Button)findViewById(R.id.remove_admin);
        remove_admin.setX(150);
        remove_admin.setY(600);

        lock_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                startLockTask();
                return false;
            }
        });

        unlock_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        // edit text
                                        if (userInput.getText().toString().equals(password)) {
                                            stopLockTask();
                                        } else {
                                            dialog.cancel();
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            Toast.makeText(this, "Volume button is disabled", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            Toast.makeText(this, "Volume button is disabled", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void onAdminRemoveClick(View v) {
        DevicePolicyManager mDPM = (DevicePolicyManager) this.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDPM.clearDeviceOwnerApp(getPackageName());
        Toast.makeText(this, "Admin Removed!", Toast.LENGTH_SHORT).show();
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

}
