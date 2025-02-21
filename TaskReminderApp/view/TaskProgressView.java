package com.example.taskreminderapp.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class TaskProgressView extends View {
    private Paint paint;
    private float progress = 0f;
    private ValueAnimator animator;

    public TaskProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20f);
        paint.setColor(Color.GREEN);
    }

    public void setProgress(float newProgress) {
        animator = ValueAnimator.ofFloat(progress, newProgress);
        animator.setDuration(1000);
        animator.addUpdateListener(animation -> {
            progress = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float sweepAngle = 360 * progress;
        canvas.drawArc(0, 0, getWidth(), getHeight(), -90, sweepAngle, false, paint);
    }
}
