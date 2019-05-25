package com.martiply.android.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import com.martiply.android.R;

public class DeviceUtils {

    public static boolean isNetworkOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static boolean isTelephonyEnabled(Context context){
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null && tm.getSimState()==TelephonyManager.SIM_STATE_READY;
    }

    public static String getUid(Context ctx){
        return Build.SERIAL;
    }

    public static void playBuzzSound(Context context){
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.notify);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
    }



}
