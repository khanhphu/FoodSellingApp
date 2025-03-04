package com.example.foodsellingapp.Model;

public class Banner {
    private String imageUrl;
    private String title;
    private String action;

    public Banner(String imageUrl, String title, String action) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.action = action;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getAction() {
        return action;
    }
}
