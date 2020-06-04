package com.oblivion.launcher;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenOnOffReceiver extends BroadcastReceiver {

    public static boolean isScreenOn;
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.v("Screen Mode", "Screen in off state");
            stopLockTask();

        } else {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                if(myKM.inKeyguardRestrictedInputMode() ) {
                    // screen is still locked
                } else {
                   Log.v("Screen Mode", "Screen is on and unlocked");
                   isScreenOn = true;

                   startLockTask();
                }
            }
        }
    }

//    public int tell(Context context, Intent intent) {
//        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
//            return 0;
//        } else {
//            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
//                KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
//                if(myKM.inKeyguardRestrictedInputMode() ) {
//                    // screen is still locked
//                } else {
//                    return 1;
//                }
//            }
//        }
//        return 2;
//    }
}
