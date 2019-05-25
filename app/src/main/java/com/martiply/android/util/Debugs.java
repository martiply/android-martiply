package com.martiply.android.util;

import android.content.Context;
import android.content.Intent;
import com.google.android.gms.maps.model.LatLng;
import com.martiply.android.activities.map.GuideHelper;
import com.martiply.android.activities.store.StoreActivity;

public class Debugs {

    public static void setFakeStore(Intent intent){
            intent.putExtra(StoreActivity.LOAD_STORE_ID, 10008);
    }

    public static int  fakeStoreId(){
        return 10017;
    }

    private static final LatLng[] boxed = {new LatLng(5.895819,93.730377), new LatLng(-9.864957,141.521819)};
    public static final LatLng thamrin = new LatLng(-6.1931326D, 106.8231138);

    public static LatLng restrictLatLng(Context context, LatLng in){
        if (in.latitude > boxed[0].latitude || in.latitude < boxed[1].latitude || in.longitude < boxed[0].longitude || in.longitude > boxed[1].longitude){
            PreferenceUtils.saveLatLng(context, thamrin.latitude, thamrin.longitude);
            return thamrin;
        }
        else {
          return in;
        }
    }
}
