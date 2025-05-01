package com.example.linker.AnalysisBase;

import com.example.linker.OtherFunction.DismantleWeb;
import com.example.linker.OtherFunction.HtmlFetcher;
import javafx.util.Pair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Wnacg extends Base{
    public Wnacg(String url) {
        super(url);
    }

    @Override
    public String getTitle(String html) {
        return DismantleWeb.analyze_wnacg_Title(html);
    }

    @Override
    public Pair<Integer, String> getNumber() {
        try {
            // 使用正則從網址中擷取 aid=xxx 或 aid-xxx
            Pattern pattern = Pattern.compile("aid-(\\d+)|aid=(\\d+)");
            Matcher matcher = pattern.matcher(url);

            if (matcher.find()) {
                // 擷取編號：看是哪一組 group 有資料
                Integer number = matcher.group(1) != null
                        ? Integer.parseInt(matcher.group(1))
                        : Integer.parseInt(matcher.group(2));

                // 抓 HTML 並解析 <title>
                String html = HtmlFetcher.fetchHtml(url);
                String title = getTitle(html);

                return new Pair<>(number, title);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
