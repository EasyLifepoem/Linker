package com.example.linker.AnalysisBase;

import javafx.util.Pair;


/**
 * 分析器：用於解析 wnacg 網站的標題與編號
 */
public abstract class Base {
    protected String url;

    public Base(String url) {
        this.url = url;
    }

    // 抽象方法：抓取標題
    public abstract String getTitle(String html);

    // 抽象方法：解析出編號與標題
    public abstract Pair<Integer, String> getNumber();
}
