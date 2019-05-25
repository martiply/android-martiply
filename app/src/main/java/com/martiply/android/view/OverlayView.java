package com.martiply.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class OverlayView extends LinearLayout {
	
	private boolean consumeTouch;
	private OverlayListener overlayListener;

	public OverlayView(Context arg0, AttributeSet arg1) {
		super(arg0, arg1);
	}	
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		return consumeTouch;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent me) {
		if (consumeTouch){
			if (me.getAction() == MotionEvent.ACTION_UP){
				if (overlayListener != null){
					overlayListener.onOverlayTouchListener();
				}
			}
		}
		return consumeTouch;
	}

	public void setConsumeTouch(boolean consumeTouch) {
		this.consumeTouch = consumeTouch;
	}
	
	public void setOverlayListener(OverlayListener overlayListener) {
		this.overlayListener = overlayListener;
	}
	
	public interface OverlayListener {
		public void onOverlayTouchListener();
		
	}

	
}
