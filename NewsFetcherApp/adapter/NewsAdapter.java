package com.example.newsfetcherapp.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.newsfetcherapp.model.NewsArticle;
import com.example.newsfetcherapp.view.AnimatedNewsCardView;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private List<NewsArticle> articles;
    private Context context;
    private OnNewsClickListener listener;

    public interface OnNewsClickListener {
        void onNewsClick(NewsArticle article);
    }

    public NewsAdapter(Context context, OnNewsClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.articles = new ArrayList<>();
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AnimatedNewsCardView cardView = new AnimatedNewsCardView(context);
        return new NewsViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        NewsArticle article = articles.get(position);
        holder.cardView.setNewsData(article);
        holder.cardView.setOnClickListener(v -> listener.onNewsClick(article));
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public void setArticles(List<NewsArticle> articles) {
        this.articles = articles;
        notifyDataSetChanged();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        AnimatedNewsCardView cardView;

        NewsViewHolder(AnimatedNewsCardView view) {
            super(view);
            cardView = view;
        }
    }
}
