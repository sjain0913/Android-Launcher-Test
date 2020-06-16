package com.oblivion.launcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// This is a launcher I am building for Boston Scientific's Enterprise Mobile Management System in Java

public class MainActivity extends AppCompatActivity {
    final Context context = this;
    protected static boolean default_app = false;
    public boolean locked = true;
    public boolean allowed = false;
    private static final String password = "abc";
    public ImageView imgWallpaper;
    public static final int RESULT_PRO_IMG=1;
    public static boolean photoPick = false;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // checking if the required permissions are granted, if not it asks
        verifyStoragePermissions(this);

        // settings app icons
        ImageView test1Icon = (ImageView) findViewById(R.id.test1Button);
        try {
            test1Icon.setImageDrawable(Helpers.getActivityIcon(this, "com.oblivion.test1", "com.oblivion.test1.MainActivity"));
        } catch (Exception e) {}
        ImageView test2Icon = (ImageView) findViewById(R.id.test2Button);
        try {
            test2Icon.setImageDrawable(Helpers.getActivityIcon(this, "com.oblivion.test2", "com.oblivion.test2.MainActivity"));
        } catch (Exception e) {}

        Button lock_btn = (Button)findViewById(com.oblivion.launcher.R.id.lock_button);
        Button unlock_btn = (Button)findViewById(com.oblivion.launcher.R.id.unlock_button);
        Button wallpaper_btn = (Button)findViewById(R.id.wallpaper);
        imgWallpaper = (ImageView) findViewById(R.id.imgWallpaper);

        // behaviors for lock, unlock, change wallpaper
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
                                        if (userInput.getText().toString().equals(password)) {
                                            locked = false;
                                        } else {
                                            dialog.cancel();
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        test1Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowed = true;
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.oblivion.test1");
                launchIntent.setAction(Intent.ACTION_MAIN);
                launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(launchIntent);
            }
        });

        test2Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowed = true;
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.oblivion.test2");
                launchIntent.setAction(Intent.ACTION_MAIN);
                launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(launchIntent);
            }
        });

        wallpaper_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoPick = true;
                Log.v("wallpaper", "change initiated");
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_PRO_IMG);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){

        if (keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void onWallpaperClick(View v) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locked && !allowed && !photoPick) {
            ActivityManager activityManager = (ActivityManager) getApplicationContext()
                    .getSystemService(Context.ACTIVITY_SERVICE);

            activityManager.moveTaskToFront(getTaskId(), 0);
        } else if (locked && allowed) {
            allowed = false;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("Data", "Data Result Code :" + requestCode);
        switch (requestCode) {
            case RESULT_PRO_IMG:
                try {
                    Log.i("Data", "Data :" + data);
                    // When an Image is picked
                    if (requestCode == RESULT_PRO_IMG && resultCode == RESULT_OK) {

                        try {
                            Uri img = data.getData();
                            String[] filepc = {MediaStore.Images.Media.DATA};
                            Cursor c = this.getContentResolver().query(img,
                                    filepc, null, null, null);
                            c.moveToFirst();
                            int cIndex = c.getColumnIndex(filepc[0]);
                            // sDecode = cursor.getString(cIndex);
                            String filePath = c.getString(cIndex);
                            c.close();
                            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                            Matrix matrix = new Matrix();
                            matrix.postRotate(0);
                            imgWallpaper.setImageBitmap(bitmap);

                            WallpaperManager myWallpaperManager = WallpaperManager
                                    .getInstance(getApplicationContext());
                            try {
                                if (bitmap != null) {
                                    myWallpaperManager.setBitmap(bitmap);
                                    Toast.makeText(MainActivity.this, "Wallpaper set Successfully !!", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Your bitmap object is null !!", Toast.LENGTH_LONG).show();
                                }
                                //set wallpaper picture from resource here

                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this, "Something went wrong !!", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "You haven't pick img", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG)
                            .show();
                }
                break;
        }
        photoPick = false;
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
