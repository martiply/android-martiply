package com.martiply.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.martiply.android.R;

public class SemiRotateButton extends FrameLayout {
	private final static int ANIM_DURATION = 400; 
	private OnSemiRotateButtonClickListener onSemiRotateButtonClickListener;
	private ImageView top;
	private ImageView bottom;
	private boolean isExpanded;
	
	public SemiRotateButton(Context context) {
		super(context);
	}
	
	public SemiRotateButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	    TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SemiRotateButton, 0, 0);
		bottom = new ImageView(context);
		top = new ImageView(context);
		Drawable botdraw = typedArray.getDrawable(R.styleable.SemiRotateButton_bottomDrawable);
		if (botdraw !=null){
			bottom.setImageDrawable(botdraw);
		}
		Drawable topdraw = typedArray.getDrawable(R.styleable.SemiRotateButton_topDrawable);
		if (topdraw != null){
			top.setImageDrawable(topdraw);
		}		
		typedArray.recycle();
		top.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				toggle();
				onSemiRotateButtonClickListener.OnToggle(SemiRotateButton.this.isExpanded);
			}
		});
		bottom.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				toggle();
				onSemiRotateButtonClickListener.OnToggle(SemiRotateButton.this.isExpanded);
			}
		});
		bottom.setVisibility(View.INVISIBLE);
		addView(bottom);
		addView(top);
		
	}
	
	private void toggle(){
		isExpanded = !isExpanded;
		top.startAnimation(initEntryTopAnim(isExpanded));
		bottom.startAnimation(initEntryBottomAnim(isExpanded));	
	}
	
	private Animation initEntryBottomAnim(boolean isOpen) {

		AnimationSet animationSet = new AnimationSet(true);
		if (isOpen) {
			animationSet.addAnimation(new RotateAnimation(-90.0F, 0.0F,Animation.RELATIVE_TO_SELF, 0.5F,Animation.RELATIVE_TO_SELF, 0.5F));
			animationSet.addAnimation(new AlphaAnimation(0.0F, 1.0F));
		} else {
			animationSet.addAnimation(new RotateAnimation(0.0F, -90.0F,Animation.RELATIVE_TO_SELF, 0.5F,	Animation.RELATIVE_TO_SELF, 0.5F));
			animationSet.addAnimation(new AlphaAnimation(1.0F, 0.0F));
		}
		animationSet.setDuration(ANIM_DURATION);
		return animationSet;
	}
	
	private Animation initEntryTopAnim(final boolean isOpen) {
		AnimationSet animationSet = new AnimationSet(true);
		if (isOpen) {
			animationSet.addAnimation(new RotateAnimation(0.0F, 90.0F, Animation.RELATIVE_TO_SELF,0.5F, Animation.RELATIVE_TO_SELF, 0.5F));
			animationSet.addAnimation(new AlphaAnimation(1.0F, 0.0F));
		}else{		
			animationSet.addAnimation(new RotateAnimation(90.0F, 0.0F, Animation.RELATIVE_TO_SELF,0.5F, Animation.RELATIVE_TO_SELF, 0.5F));
			animationSet.addAnimation(new AlphaAnimation(0.0F, 1.0F));
		}
		animationSet.setDuration(ANIM_DURATION);
		animationSet.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				top.setVisibility(View.VISIBLE);
				bottom.setVisibility(View.VISIBLE);
				}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				postDelayed(new Runnable() {
					public void run() {
						if (isOpen){
							top.setVisibility(View.GONE);
							bottom.setVisibility(View.VISIBLE);
						}else{
							top.setVisibility(View.VISIBLE);
							bottom.setVisibility(View.GONE);
						}						
					}
				}, 0L);

			}
		});		
		return animationSet;
	}
	

	public void setOnSemiRotateButtonClickListener(OnSemiRotateButtonClickListener onSemiRotateButtonClickListener) {
		this.onSemiRotateButtonClickListener = onSemiRotateButtonClickListener;
	}
	
	
	public static abstract interface OnSemiRotateButtonClickListener {
		public void OnToggle(boolean isExpanded);

	}

	
	
}
