package com.oblivion.launcher;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.KeyguardManager;
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
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Display;
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
    protected static final String[] APP_PACKAGES = {"com.oblivion.test1", "com.oblivion.test2", "com.oblivion.launcher"};


    final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    private static final int REQUEST_CODE = 0;
    DevicePolicyManager mDPM;
    ComponentName mAdminName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().getDecorView().setSystemUiVisibility(flags);
        setContentView(R.layout.activity_main);


        // Ask for permission to be admin
        mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        // Set DeviceAdminDemo Receiver for active the component with different option
        mAdminName = new ComponentName(this, AdminReceiver.class);

        if (!mDPM.isAdminActive(mAdminName)) {
            // try to become active
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to secure your application.");
            startActivityForResult(intent, REQUEST_CODE);
        }
        else
        {
            // Already is a device administrator, can do security operations now.
        }

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        ScreenOnOffReceiver onoffReceiver = new ScreenOnOffReceiver();
        registerReceiver(onoffReceiver, filter);
        startLockTask();

        // Setting Icon Positions
        ImageView test1Icon = (ImageView) findViewById(R.id.test1Button);
        try {
            test1Icon.setImageDrawable(Helpers.getActivityIcon(this, "com.oblivion.test1", "com.oblivion.test1.MainActivity"));
        } catch (Exception e) {}
        test1Icon.setX(50);
        test1Icon.setY(50);

        ImageView test2Icon = (ImageView) findViewById(R.id.test2Button);
        try {
            test2Icon.setImageDrawable(Helpers.getActivityIcon(this, "com.oblivion.test2", "com.oblivion.test2.MainActivity"));
        } catch (Exception e) {}
        test2Icon.setX(250);
        test2Icon.setY(50);


        Button lock_btn = (Button)findViewById(com.oblivion.launcher.R.id.lock_button);
        lock_btn.setX(50);
        lock_btn.setY(400);

        Button unlock_btn = (Button)findViewById(com.oblivion.launcher.R.id.unlock_button);
        unlock_btn.setX(250);
        unlock_btn.setY(400);

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

        if (keyCode == KeyEvent.KEYCODE_HOME && ScreenOnOffReceiver.screenOn) {
            Toast.makeText(this, "Home button is disabled", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_MENU && ScreenOnOffReceiver.screenOn) {
            Toast.makeText(this, "Menu disabled", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_BACK && ScreenOnOffReceiver.screenOn) {
            Toast.makeText(this, "Back disabled", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        // If the screen is off then the device has been locked
        if (!ScreenOnOffReceiver.screenOn) {
            stopLockTask();
            Log.v("Screen off", "Unpinned");
            ScreenOnOffReceiver.screenOn = false;
            Log.v("Screen off", "Screen off");
        }
    }

    // when app comes back, only pin screen again when app is unlocked
    @Override
    protected void onResume() {
        super.onResume();
        if (ScreenOnOffReceiver.screenOn) {
            Log.v("Screen unlocked", "Screen unlocked");
            ScreenOnOffReceiver.screenOn = true;
            startLockTask();
            Log.v("Screen unlocked", "pinned");
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
}
