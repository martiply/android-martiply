package com.martiply.android.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class MenuProgressView extends View {
    private int mSize;
    private Paint mPaint;
    private RectF mRect;
    private float mIndeterminateSweep;
    private float mstartAngle;
    private ValueAnimator frontEndExtend;

    public MenuProgressView(Context context) {
        super(context);
        init();
    }

    public MenuProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MenuProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4f);
        mPaint.setStrokeCap(Paint.Cap.BUTT);
        mRect = new RectF(25, 25, 70, 70);
        mIndeterminateSweep = 10f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        canvas.drawArc(mRect, mstartAngle, mIndeterminateSweep, false, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int xPad = getPaddingLeft() + getPaddingRight();
        int yPad = getPaddingTop() + getPaddingBottom();
//        int width = getMeasuredWidth() - xPad;
//        int height = getMeasuredHeight() - yPad;
//        mSize = (width < height) ? width : height;
        mSize = 90;
        setMeasuredDimension(mSize + xPad, mSize + yPad);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (frontEndExtend == null)
        animateArch();
    }

    private void animateArch() {
        frontEndExtend = ValueAnimator.ofFloat(0, 360);
        frontEndExtend.setDuration(2500);
//        frontEndExtend.setInterpolator(new LinearInterpolator());
        frontEndExtend.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mstartAngle = (Float) animation.getAnimatedValue();
                mIndeterminateSweep += 2;
                if (mIndeterminateSweep > 360)
                    mIndeterminateSweep = 10f;
                invalidate();
            }
        });
        frontEndExtend.start();
        frontEndExtend.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animateArch();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                frontEndExtend.removeAllUpdateListeners();
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }


}