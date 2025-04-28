package com.example.linker.OtherFunction;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * HtmlFetcher 類
 * 專門負責從指定 URL 抓取網頁 HTML 原始碼
 */
public class HtmlFetcher {

    /**
     * 連線到指定網址並取得 HTML 字串
     * @param url 網址
     * @return 成功返回 HTML 字串，失敗返回空字串
     */
    public static String fetchHtml(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .header("Referer", "https://18comic.vip/") // ⭐ 新增 Referer
                    .header("Accept-Language", "zh-TW,zh;q=0.9,en;q=0.8") // ⭐ 模擬台灣地區
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.out.println("HTTP 錯誤：" + response.statusCode());
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
