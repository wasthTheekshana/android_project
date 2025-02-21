package com.example.userprofilefetcher.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class AvatarView extends View {
    private Paint paint;
    private String initials = "";
    private float animProgress = 0f;
    private ValueAnimator animator;
    private int avatarColor = Color.BLUE;

    public AvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    public void setUserDetails(String name, String color) {
        if (name != null && !name.isEmpty()) {
            String[] parts = name.split(" ");
            initials = (parts[0].charAt(0) + (parts.length > 1 ? String.valueOf(parts[1].charAt(0)) : "")).toUpperCase();
        }
        avatarColor = Color.parseColor(color);

        // Animate the avatar appearance
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(1000);
        animator.addUpdateListener(animation -> {
            animProgress = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float radius = Math.min(centerX, centerY) * animProgress;

        // Draw circle
        paint.setColor(avatarColor);
        canvas.drawCircle(centerX, centerY, radius, paint);

        // Draw initials
        paint.setColor(Color.WHITE);
        paint.setTextSize(radius);
        canvas.drawText(initials, centerX, centerY + radius/3, paint);
    }
}
