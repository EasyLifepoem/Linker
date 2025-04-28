package com.example.linker.OtherFunction;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DismantleWeb {

    /**
     * 從 HTML 中提取 <title> 的純文字
     * @param html 網頁的完整 HTML 字串
     * @return title 內容，找不到就回傳空字串
     */
    public static String analyze_wnacg_Title(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }
        // 建立正則表達式：
        // (?i)     ➔ 忽略大小寫，例如 <title>、<TITLE> 都可以被抓到
        // <title>  ➔ 標籤開頭
        // (.*?)    ➔ 中間的任意內容，非貪婪模式（只取第一個出現的）
        // </title> ➔ 標籤結尾
        Pattern pattern = Pattern.compile("(?i)<title>(.*?)</title>");
        // 透過 Matcher 進行比對
        Matcher matcher = pattern.matcher(html);
        // 如果有找到符合的 <title> ... </title> 結構
        if (matcher.find()) {
            // matcher.group(1) 拿到括號內捕捉的內容（就是 title 裡面的文字）
            return matcher.group(1).trim();
        } else {
            return "";
        }
    }

    /**
     * 從 HTML 中提取 <meta property="og:title"> 的 content 內容
     * 特別用於 18comic 等網站
     * 自動進行 URL 編碼
     * @param html 網頁的完整 HTML 字串
     * @return 編碼後的 og:title 內容，找不到就回傳空字串
     */
    public static String analyze_comic_Title(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }
        Pattern pattern = Pattern.compile(
                "<meta\\s+property=[\"']og:title[\"']\\s+content=[\"'](.*?)[\"']",
                Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            String title = matcher.group(1).trim();
            return encode(title); // ⭐ 這裡呼叫 encode()
        } else {
            return "";
        }
    }

    /**
     * 將字串進行 URL 編碼
     * @param input 原始字串
     * @return URL 編碼後的字串
     */
    public static String encode(String input) {
        try {
            return URLEncoder.encode(input, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return input; // 出錯就回傳原始字串
        }
    }
}
