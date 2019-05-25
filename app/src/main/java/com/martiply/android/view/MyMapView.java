package com.martiply.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;


public class MyMapView extends MapView {
    private long lastTouchTime = -1;
    private Listener listener;

    public MyMapView(Context context) {
        super(context);
    }

    public MyMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyMapView(Context context, GoogleMapOptions options) {
        super(context, options);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            long thisTime = System.currentTimeMillis();
            if (thisTime - lastTouchTime < ViewConfiguration.getDoubleTapTimeout()) {
                // Double tap
                if (listener != null){
                    listener.onDoubleClicked(ev);
                }
                lastTouchTime = -1;
                return true;
            } else {
                // Too slow
                lastTouchTime = thisTime;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    public interface Listener {
        public void onDoubleClicked(MotionEvent motionEvent);
    }
}