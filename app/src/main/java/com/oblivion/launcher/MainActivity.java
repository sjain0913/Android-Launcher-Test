package com.oblivion.launcher;

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

        ImageView test2Icon = (ImageView) findViewById(R.id.test2Button);
        try {
            test2Icon.setImageDrawable(Helpers.getActivityIcon(this, "com.oblivion.test2", "com.oblivion.test2.MainActivity"));
        } catch (Exception e) {}


        Button lock_btn = (Button)findViewById(com.oblivion.launcher.R.id.lock_button);
//        lock_btn.setX(50);
//        lock_btn.setY(400);

        Button unlock_btn = (Button)findViewById(com.oblivion.launcher.R.id.unlock_button);
//        unlock_btn.setX(250);
//        unlock_btn.setY(400);

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
}
