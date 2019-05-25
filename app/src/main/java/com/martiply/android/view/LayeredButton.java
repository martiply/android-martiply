package com.martiply.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.martiply.android.R;

public class LayeredButton extends FrameLayout {
	private int clickedColor;
	private int normalColor;
	private ImageView icon;
	private View bottomView;
	private View.OnClickListener onClickListener;

	public LayeredButton(Context paramContext) {
		super(paramContext);
	}


	public LayeredButton(Context context, AttributeSet attrs){
	    super(context, attrs);

	    TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LayeredButton, 0, 0);
	    this.clickedColor = typedArray.getColor(R.styleable.LayeredButton_clickedColor, getResources().getColor(R.color.grey_lt1));
	    this.normalColor = typedArray.getColor(R.styleable.LayeredButton_normalColor, getResources().getColor(android.R.color.transparent));

	    bottomView = new View(context);
		bottomView.setBackgroundColor(normalColor);
	    addView(bottomView);

	    icon = new ImageView(context);
	    int imgWidth = (int) typedArray.getDimension(R.styleable.LayeredButton_imgWidth, 0.0F);
	    int imgHeight = (int) typedArray.getDimension(R.styleable.LayeredButton_imgHeight, 0.0F);
	    FrameLayout.LayoutParams imgparams = new FrameLayout.LayoutParams(imgWidth, imgHeight, Gravity.CENTER);
	    icon.setLayoutParams(imgparams);
	    Drawable localDrawable = typedArray.getDrawable(R.styleable.LayeredButton_buttonDrawable);
	    if (localDrawable != null){
	    	icon.setImageDrawable(localDrawable);
	    }
	    addView(icon);
	    typedArray.recycle();
	    setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()){
				case MotionEvent.ACTION_DOWN :
				case MotionEvent.ACTION_MOVE : bottomView.setBackgroundColor(clickedColor); break;
				case MotionEvent.ACTION_UP : if (onClickListener != null)
					onClickListener.onClick(v);
				default : bottomView.setBackgroundColor(normalColor); break;					
				}
				return true;
			}
		});
	}

	public void setButtonDrawable(int drawableRes){
		icon.setImageResource(drawableRes);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		bottomView.getLayoutParams().height = h;
		bottomView.getLayoutParams().height= w;
		bottomView.setBackgroundColor(normalColor);		
	}

	public Drawable getImage() {
		int i = getChildCount();
		Drawable localDrawable = null;
		if (i == 2)
			localDrawable = ((ImageView) getChildAt(1)).getDrawable();
		return localDrawable;
	}

	public void setImage(int imgres) {
		if (getChildCount() == 2)
			((ImageView) getChildAt(1)).setImageDrawable(getContext().getResources().getDrawable(imgres));
	}

	public void setImage(Drawable paramDrawable) {
		if (getChildCount() == 2)
			((ImageView) getChildAt(1)).setImageDrawable(paramDrawable);
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		this.onClickListener = l;
	}

//	public static abstract interface OnLayeredButtonClickListener {
//		public abstract void onClicked();
//	}


}