package com.martiply.android.view;

import android.content.Context;
import androidx.appcompat.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class PassTouchEventToolbar extends Toolbar {
    public PassTouchEventToolbar(Context context) {
        super(context);
    }

    public PassTouchEventToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PassTouchEventToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

}
