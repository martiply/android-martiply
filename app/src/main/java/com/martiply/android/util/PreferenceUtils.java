package com.martiply.android.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.android.gms.maps.model.LatLng;

public class PreferenceUtils {
	private static final String KEY_LATITUDE = "KEY_LATITUDE";
	private static final String KEY_LONGITUDE = "KEY_LONGITUDE";

	public static LatLng getLatLng(Context paramContext) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(paramContext);
		return new LatLng(Double.longBitsToDouble(sharedPreferences.getLong(KEY_LATITUDE, 0L)), Double.longBitsToDouble(sharedPreferences.getLong(KEY_LONGITUDE, 0L)));
	}


	@SuppressLint("ApplySharedPref")
	static void saveLatLng(Context paramContext, double paramDouble1, double paramDouble2) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(paramContext);
		sharedPreferences.edit().putLong(KEY_LATITUDE, Double.doubleToLongBits(paramDouble1)).commit();
		sharedPreferences.edit().putLong(KEY_LONGITUDE, Double.doubleToLongBits(paramDouble2)).commit();
	}


}