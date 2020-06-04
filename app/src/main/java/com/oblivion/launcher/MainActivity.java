package com.oblivion.launcher;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    public static boolean isScreenOn;
    final Context context = this;
    private EditText result;
    private static boolean isPinned;
    private static final String password = "abc";
    public static final String APP1_PACKAGE = "com.oblivion.test1";
    public static final String APP2_PACKAGE = "com.oblivion.test2";
    public static final String[] APP_PACKAGES = {APP1_PACKAGE, APP2_PACKAGE, "com.oblivion.launcher"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        ScreenOnOffReceiver onoffReceiver = new ScreenOnOffReceiver();
        registerReceiver(onoffReceiver, filter);
        startLockTask();
        isPinned = true;

        // Icons & Buttons
        ImageView test1Icon = (ImageView) findViewById(R.id.test1Button);
        try {
            test1Icon.setImageDrawable(Helpers.getActivityIcon(this, "com.oblivion.test1", "com.oblivion.test1.MainActivity"));
        } catch(Exception e) {}
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

        Button remove_admin = (Button)findViewById(R.id.remove_admin);
        remove_admin.setX(150);
        remove_admin.setY(600);

        // Button Functions
        lock_btn.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                startLockTask();
                isPinned = true;
                return false;
            }
        });

        unlock_btn.setOnClickListener(new View.OnClickListener() {
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
                                            isPinned = false;
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

    // Button Presses
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


    // Disabling the use of keys
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_HOME && isPinned == true) {
            Toast.makeText(this, "Home button is disabled", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_MENU && isPinned == true) {
            Toast.makeText(this, "Menu button is disabled", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_BACK && isPinned == true) {
            Toast.makeText(this, "Back button is disabled", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_POWER) {
            Log.v("Screen Mode", "power button pressed");
            if (isScreenOn) {
                Log.v("Screen Mode", "power pressed to turn on");
                startLockTask();
            } else if (!isScreenOn) {
                Log.v("Screen Mode", "power pressed to turn off");
                stopLockTask();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
