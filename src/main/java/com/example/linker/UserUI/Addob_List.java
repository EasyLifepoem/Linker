package com.example.linker.UserUI;

import com.example.linker.LineModel;
import com.example.linker.HelloApplication;
import com.example.linker.YamlService;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import com.example.linker.OtherFunction.DismantleWeb;
import com.example.linker.OtherFunction.HtmlFetcher;

import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Addob_List {

    YamlService yml;

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
            String name = DismantleWeb.analyze_Title(html);

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

            System.out.println("新增連結成功！");

            // 儲存更新後的資料到 YAML
            YamlService.writeYaml(model);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("網址解析失敗！");
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
    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("訊息");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * 專門處理 wnacg 類型網址
     */
    private Integer Wnacg_Number(String url) {
        try {
            Pattern pattern = Pattern.compile("aid-(\\d+)|aid=(\\d+)");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                return matcher.group(1) != null ? Integer.parseInt(matcher.group(1)) : Integer.parseInt(matcher.group(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 專門處理 18comic 類型網址
     */
    private Integer comic_Number(String url) {
        try {
            Pattern pattern = Pattern.compile("/photo/([0-9]+)");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 專門處理 nhentai 類型網址
     */
    private Integer Nhentai_Number(String url) {
        try {
            Pattern pattern = Pattern.compile("/g/([0-9]+)");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * ⭐ 把特殊處理分類集中在這裡
     */
    private void CheckWebTypes(String type, String url, LineModel.LinkEntry newLink) {
        switch (type.toLowerCase()) {
            case "wnacg" -> {
                Integer number = Wnacg_Number(url);
                if (number != null) {
                    newLink.setNumber(number);
                }
            }
            case "18comic" -> {
                Integer number = comic_Number(url);
                if (number != null) {
                    newLink.setNumber(number);
                }
            }
            case "nhentai" -> {
                Integer number = Nhentai_Number(url);
                if (number != null) {
                    newLink.setNumber(number);
                }
            }
            default -> {
                // 其他網站，不做特別處理
            }
        }
    }
}
