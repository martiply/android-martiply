package com.martiply.android.util;

import android.content.Context;
import android.text.format.DateFormat;
import com.martiply.android.R;

import java.util.Calendar;
import java.util.Date;

public class StringUtils {
	

    public static CharSequence millisToDate(Context context, long millis){
        CharSequence date;
		date = DateFormat.format(context.getString(R.string.date_full), new Date(millis));
		return date;
    }

	public static int getYear() {
		Calendar now = Calendar.getInstance();
		return now.get(Calendar.YEAR);
	}

	public static String getFriendlyCurrencyString(Context ctx, String code, String value){
        switch (code.toUpperCase()){
            case "IDR" : return ctx.getString(R.string.currency_short_rp, value);
            case "JPY" : return ctx.getString(R.string.currency_short_yen, value);
            default: return value;
        }
    }
}
