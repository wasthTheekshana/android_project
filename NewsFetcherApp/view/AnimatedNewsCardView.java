package com.example.newsfetcherapp.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.newsfetcherapp.R;
import com.example.newsfetcherapp.model.NewsArticle;

public class AnimatedNewsCardView extends CardView {
    private float alpha = 0f;
    private ValueAnimator fadeAnimator;
    private TextView titleView;
    private TextView descriptionView;

    public AnimatedNewsCardView(Context context) {
        super(context);
        init(context);
    }

    public AnimatedNewsCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        // Inflate layout
        LayoutInflater.from(context).inflate(R.layout.news_card_layout, this, true);
        titleView = findViewById(R.id.newsTitle);
        descriptionView = findViewById(R.id.newsDescription);

        // Initial setup
        setAlpha(0f);
        // Use direct dp values instead of dimension resources
        setRadius(dpToPx(context, 8));  // 8dp radius
        setCardElevation(dpToPx(context, 4));  // 4dp elevation

        // Set padding and margins
        int padding = dpToPx(context, 16);
        setContentPadding(padding, padding, padding, padding);

        // Set layout parameters with margins
        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        int margin = dpToPx(context, 8);
        layoutParams.setMargins(margin, margin, margin, margin);
        setLayoutParams(layoutParams);

        // Set background color
        setCardBackgroundColor(Color.WHITE);
    }

    // Helper method to convert dp to pixels
    private int dpToPx(Context context, float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    public void setNewsData(NewsArticle article) {
        titleView.setText(article.getTitle());
        descriptionView.setText(article.getDescription());

        // Start fade-in animation
        fadeAnimator = ValueAnimator.ofFloat(0f, 1f);
        fadeAnimator.setDuration(500);
        fadeAnimator.addUpdateListener(animation -> {
            setAlpha((float) animation.getAnimatedValue());
        });
        fadeAnimator.start();
    }
}
