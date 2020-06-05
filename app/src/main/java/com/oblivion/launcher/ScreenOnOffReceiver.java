package com.oblivion.launcher;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenOnOffReceiver extends BroadcastReceiver {

    public static boolean screenOn;
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.v("Screen Mode", "Screen in off state");
            //stopLockTask();

        } else {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Log.v("Screen Mode", "Screen in on state");
                KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                if( myKM.isKeyguardLocked()) {
                    //it is locked
                } else {
                    Log.v("Phone Lock", "Phone Unlocked");
                }
            }
        }
    }
}
