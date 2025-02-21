package com.example.newsfetcherapp.model;

public class NewsArticle {
    private String title;
    private String description;
    private String url;
    private boolean isBreaking;
    private String publishedAt;

    public NewsArticle(String title, String description, String url,
                       boolean isBreaking, String publishedAt) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.isBreaking = isBreaking;
        this.publishedAt = publishedAt;
    }

    // Add getters and setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isBreaking() {
        return isBreaking;
    }

    public void setBreaking(boolean breaking) {
        isBreaking = breaking;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }
}
