package com.example.linker.AnalysisBase;

import javafx.util.Pair;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 分析器：用於解析 18comic 網址中的編號與標題
 * ⭐ 注意：此網站從 URL 本身就能提取資料，不需使用 HTML
 */
public class comic extends Base {

    public comic(String url) {
        super(url);
    }

    @Override
    public String getTitle(String html) {
        // 不從 HTML 取得，直接回傳 null
        return null;
    }

    @Override
    public Pair<Integer, String> getNumber() {
        try {
            // 擷取編號：/album/123456
            Pattern pattern = Pattern.compile("/album/([0-9]+)");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                Integer number = Integer.parseInt(matcher.group(1));

                // 擷取名稱：取網址最後一段並 URL 解碼
                int lastSlash = url.lastIndexOf('/');
                if (lastSlash != -1 && lastSlash + 1 < url.length()) {
                    String encoded = url.substring(lastSlash + 1);
                    String decoded = URLDecoder.decode(encoded, StandardCharsets.UTF_8);
                    return new Pair<>(number, decoded);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
