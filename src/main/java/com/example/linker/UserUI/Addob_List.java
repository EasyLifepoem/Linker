package com.example.linker.UserUI;

import com.example.linker.LineModel;
import com.example.linker.HelloApplication;
import com.example.linker.YamlService;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import com.example.linker.OtherFunction.DismantleWeb;
import com.example.linker.OtherFunction.HtmlFetcher;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.example.linker.AnalysisBase.Base;
import com.example.linker.AnalysisBase.Wnacg;
import com.example.linker.AnalysisBase.comic;


import javafx.scene.text.Text;
import javafx.util.Pair;


public class Addob_List {

    @FXML
    private Text statusText;

    @FXML
    private TextArea linkInput; // 綁定 FXML 中輸入網址的 TextArea 元件

    @FXML
    private void initialize() {
        // 初始化方法，目前留空
    }

    /**
     * 當使用者點擊「新增連結」按鈕時呼叫
     */
    @FXML
    private void onAddLink() {
        // 讀取使用者輸入
        String userInput = linkInput.getText().trim();
        System.out.println("輸入網址：" + userInput);

        // 基本檢查：必須是 http 或 https 開頭
        if (!userInput.startsWith("http")) {
            System.out.println("輸入格式錯誤！必須是 http 或 https 開頭的網址。");
            return;
        }

        try {
            // 使用 Java URL 類解析網址
            URL uri = new URL(userInput);
            String host = uri.getHost();// 取得 host，例如 www.xxxxx.com
            if (host == null || !host.contains(".")) {
                System.out.println("網址格式異常！");
                return;
            }
            // 解析主域名作為 type
            String type = extractTypeFromHost(host);
            // 保留原始輸入的網址
            String url = userInput;

            //新增這段：抓取 HTML
            String html = HtmlFetcher.fetchHtml(url);
            //用 DismantleWeb 分析 title
            String name = DismantleWeb.analyze_wnacg_Title(html);

            // 若未取得 title，則以網址路徑最後一段作為備案
            if (name == null || name.isEmpty()) {
                // 如果沒抓到 title，退而求其次，用網址最後一段
                String path = uri.getPath();
                name = path.substring(path.lastIndexOf("/") + 1);
            }

            // 輸出解析結果
            System.out.println("解析結果：");
            System.out.println("type: " + type);
            System.out.println("name: " + name);
            System.out.println("url: " + url);

            // 取得全域的資料模型
            LineModel model = HelloApplication.Global_LineModel;
            if (model == null) {
                System.out.println("資料模型不存在！");
                return;
            }

            // 尋找是否已存在相同 type 的 WebType
            LineModel.WebType targetWebType = model.getWebType().stream()
                    .filter(webType -> webType.getType().equals(type))
                    .findFirst()
                    .orElse(null);

            // 若找不到，則建立新的 WebType
            if (targetWebType == null) {
                targetWebType = new LineModel.WebType();
                targetWebType.setType(type);
                targetWebType.setLinks(new ArrayList<>());
                model.getWebType().add(targetWebType);
                System.out.println("建立新的 WebType: " + type);
            }

            // 建立新的 LinkEntry
            LineModel.LinkEntry newLink = new LineModel.LinkEntry();
            newLink.setName(name);
            newLink.setURL(url);
            //放入對應的Number
            CheckWebTypes(type, url, newLink);
            // 特殊類型網站額外處理（提取 ID）
            targetWebType.getLinks().add(newLink);

            showStatus("新增成功！",true);
            // 儲存更新後的資料到 YAML
            YamlService.writeYaml(model);

        } catch (Exception e) {
            e.printStackTrace();
            showStatus("解析失敗！",false);
        }
    }


    /**
     * 從 host 中提取主域名（去掉 www.）
     */
    private String extractTypeFromHost(String host) {
        if (host.startsWith("www.")) {
            host = host.substring(4);
        }
        int dotIndex = host.indexOf('.');
        if (dotIndex != -1) {
            return host.substring(0, dotIndex);
        }
        return host;
    }

    /**
     * 顯示提示訊息
     */
    private void showStatus(String message, boolean success) {
        if (statusText != null) {
            statusText.setText(message);
            if (success) {
                statusText.setStyle("-fx-fill: green;");
            } else {
                statusText.setStyle("-fx-fill: red;");
            }
        } else {
            System.out.println("狀態欄位未綁定：" + message);
        }
    }

    /**
     * 根據網站類型（type）對網址（url）進行特別處理：
     * - 若為特別支援的網站（如 wnacg、18comic、nhentai），則解析出編號與標題
     * - 若無特別支援，則直接將 URL 當作名稱
     *
     * @param type 網站類型（從 host 中提取出來的）
     * @param url  使用者輸入的完整網址
     * @param newLink 要填充資料的 LinkEntry 物件
     */
    private static final Map<String, Class<? extends Base>> analyzerMap = Map.of(
            "wnacg", Wnacg.class,
            "18comic", comic.class
    );

    /**
     * 根據網站類型使用對應的分析器進行網址解析。
     * 若不支援該網站，則使用網址末段作為標題。
     */
    private void CheckWebTypes(String type, String url, LineModel.LinkEntry newLink) {
        try {
            Class<? extends Base> analyzerClass = analyzerMap.get(type.toLowerCase());

            if (analyzerClass != null) {
                // 透過反射建立對應分析器
                Base analyzer = analyzerClass.getConstructor(String.class).newInstance(url);
                Pair<Integer, String> result = analyzer.getNumber();

                if (result != null) {
                    newLink.setNumber(result.getKey());
                    newLink.setName(result.getValue());
                    return;
                }
            } else if (type.equalsIgnoreCase("nhentai")) {
                // 特殊處理 nhentai
                Pattern pattern = Pattern.compile("/g/([0-9]+)");
                Matcher matcher = pattern.matcher(url);
                if (matcher.find()) {
                    int number = Integer.parseInt(matcher.group(1));
                    newLink.setNumber(number);
                    newLink.setName(url); // 沿用網址作為名稱
                    return;
                }
            }

            // fallback：不支援的類型
            newLink.setName(url);

        } catch (Exception e) {
            e.printStackTrace();
            newLink.setName(url);
        }
    }
}
