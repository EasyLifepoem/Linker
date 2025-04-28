package com.example.linker.UserUI;

/**
 * ListView 顯示用的內部類
 */
public class UserUI {
    private final String title; // 顯示的標題
    private final String url;   // 真實的網址

    public UserUI(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return title; // ListView 只顯示 title
    }
}
