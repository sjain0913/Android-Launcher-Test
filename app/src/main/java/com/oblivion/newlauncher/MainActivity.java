package com.oblivion.newlauncher;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final Context context = this;
    protected static boolean default_app = false;
    public static boolean locked = true;
    public static boolean allowed = false;
    private static final String password = "abc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        isMyAppLauncherDefault();
        if (default_app) {

        } else {
            PackageManager packageManager = context.getPackageManager();
            ComponentName componentName = new ComponentName(context, com.oblivion.newlauncher.FakeLauncherActivity.class);
            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

            Intent selector = new Intent(Intent.ACTION_MAIN);
            selector.addCategory(Intent.CATEGORY_HOME);
            selector.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(selector);

            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
        }

        Button lock_btn = (Button)findViewById(com.oblivion.newlauncher.R.id.lock_button);
        lock_btn.setX(50);
        lock_btn.setY(400);

        Button unlock_btn = (Button)findViewById(com.oblivion.newlauncher.R.id.unlock_button);
        unlock_btn.setX(250);
        unlock_btn.setY(400);

        lock_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               locked = true;
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
                                            locked = false;
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

    public void onTest1ButtonClick(View v) {
        allowed = true;
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.oblivion.test1");
        startActivity(launchIntent);
    }

    public void onTest2ButtonClick(View v) {
        allowed = true;
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.oblivion.test2");
        startActivity(launchIntent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){

        if (keyCode == KeyEvent.KEYCODE_HOME) {
            Log.v("button", "home pressed");
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_MENU) {
            Log.v("button", "menu pressed");
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.v("button", "back pressed");
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locked && !allowed) {
            ActivityManager activityManager = (ActivityManager) getApplicationContext()
                    .getSystemService(Context.ACTIVITY_SERVICE);

            activityManager.moveTaskToFront(getTaskId(), 0);
        } else if (locked && allowed) {
            allowed = false;
        }
    }

    /**
     * method checks to see if app is currently set as default launcher
     * @return boolean true means currently set as default, otherwise false
     */
    private boolean isMyAppLauncherDefault() {
        final IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
        filter.addCategory(Intent.CATEGORY_HOME);

        List<IntentFilter> filters = new ArrayList<IntentFilter>();
        filters.add(filter);

        final String myPackageName = getPackageName();
        List<ComponentName> activities = new ArrayList<ComponentName>();
        final PackageManager packageManager = (PackageManager) getPackageManager();

        packageManager.getPreferredActivities(filters, activities, null);

        for (ComponentName activity : activities) {
            if (myPackageName.equals(activity.getPackageName())) {
                default_app = true;
                return true;
            }
        }
        default_app = false;
        return false;
    }
}
