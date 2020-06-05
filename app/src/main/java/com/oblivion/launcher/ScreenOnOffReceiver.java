package com.oblivion.launcher;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.util.Log;
import android.view.Display;

public class ScreenOnOffReceiver extends BroadcastReceiver {

    public static boolean screenOn;
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.v("Screen Mode", "Screen in off state");
            screenOn = false;
            //stopLockTask();

        } else {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Log.v("Screen Mode", "Screen in on state");
                screenOn = true;
                KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                if(!myKM.isKeyguardLocked()) {
                    Log.v("Phone Lock", "Phone Unlocked");
                }
            }
        }
    }

    public boolean displayOn(Context context) {
        DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        for (Display display : dm.getDisplays()) {
            if (display.getState() != Display.STATE_OFF) {
                return true;
            }
        }
        return false;
    }

}
