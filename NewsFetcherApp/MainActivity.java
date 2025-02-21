package com.example.newsfetcherapp;

import android.animation.ObjectAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.newsfetcherapp.adapter.NewsAdapter;
import com.example.newsfetcherapp.connection.NewsApiClient;
import com.example.newsfetcherapp.db.NewsPreferences;
import com.example.newsfetcherapp.model.NewsArticle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private NewsPreferences newsPrefs;
    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private FloatingActionButton refreshFab;
    private ObjectAnimator rotationAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsPrefs = new NewsPreferences(this);
        initializeViews();
        setupRecyclerView();
        setupAnimations();
        fetchNews();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.newsRecyclerView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        refreshFab = findViewById(R.id.refreshFab);

        swipeRefresh.setOnRefreshListener(this::fetchNews);
        refreshFab.setOnClickListener(v -> {
            rotationAnimator.start();
            fetchNews();
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsAdapter(this, article -> {
            // Open article in browser
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl()));
            startActivity(browserIntent);

            // Save last viewed headline
            newsPrefs.saveLastHeadline(article.getTitle());
        });
        recyclerView.setAdapter(adapter);
    }

    private void setupAnimations() {
        rotationAnimator = ObjectAnimator.ofFloat(refreshFab, "rotation", 0f, 360f);
        rotationAnimator.setDuration(1000);
        rotationAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
    }

    private void fetchNews() {
        new Thread(() -> {
            String jsonData = NewsApiClient.fetchNews();
            try {
                JSONObject json = new JSONObject(jsonData);
                JSONArray articles = json.getJSONArray("articles");
                List<NewsArticle> newsList = new ArrayList<>();

                for (int i = 0; i < articles.length(); i++) {
                    JSONObject article = articles.getJSONObject(i);
                    NewsArticle newsArticle = new NewsArticle(
                            article.getString("title"),
                            article.getString("description"),
                            article.getString("url"),
                            article.optBoolean("breaking", false),
                            article.getString("publishedAt")
                    );
                    newsList.add(newsArticle);

                    if (newsArticle.isBreaking()) {
                        showBreakingNewsNotification(newsArticle);
                    }
                }

                runOnUiThread(() -> {
                    adapter.setArticles(newsList);
                    swipeRefresh.setRefreshing(false);
                    rotationAnimator.cancel();
                    refreshFab.setRotation(0f);
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showBreakingNewsNotification(NewsArticle article) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "breaking_news",
                    "Breaking News",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl()));
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, browserIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "breaking_news")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Breaking News")
                .setContentText(article.getTitle())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(1, builder.build());
    }
}