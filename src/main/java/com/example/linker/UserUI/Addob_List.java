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
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private Pair<Integer, String> Wnacg_Number(String url) {
        try {
            // 抓數字
            Pattern pattern = Pattern.compile("aid-(\\d+)|aid=(\\d+)");
            Matcher matcher = pattern.matcher(url);

            if (matcher.find()) {
                Integer number = matcher.group(1) != null ? Integer.parseInt(matcher.group(1)) : Integer.parseInt(matcher.group(2));

                // 同時抓 HTML，然後解析<title>
                String html = HtmlFetcher.fetchHtml(url);
                String title = DismantleWeb.analyze_wnacg_Title(html);

                return new Pair<>(number, title);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Pair<Integer, String> comic_Number(String url) {
        try {
            Pattern pattern = Pattern.compile("/album/([0-9]+)");
            Matcher matcher = pattern.matcher(url);

            if (matcher.find()) {
                Integer number = Integer.parseInt(matcher.group(1));

                // ⭐ 這裡改：直接從 URL 後面抓 name
                int lastSlash = url.lastIndexOf('/');
                if (lastSlash != -1 && lastSlash + 1 < url.length()) {
                    String encodedName = url.substring(lastSlash + 1);
                    String decodedName = URLDecoder.decode(encodedName, StandardCharsets.UTF_8);
                    return new Pair<>(number, decodedName);
                }
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
     * 根據網站類型（type）對網址（url）進行特別處理：
     * - 若為特別支援的網站（如 wnacg、18comic、nhentai），則解析出編號與標題
     * - 若無特別支援，則直接將 URL 當作名稱
     *
     * @param type 網站類型（從 host 中提取出來的）
     * @param url  使用者輸入的完整網址
     * @param newLink 要填充資料的 LinkEntry 物件
     */
    private void CheckWebTypes(String type, String url, LineModel.LinkEntry newLink) {
        // 用來裝解析結果：包含編號 (Integer) 和標題 (String)
        Optional<Pair<Integer, String>> normalCase = Optional.empty();

        // 根據不同網站類型，選擇不同解析方式
        switch (type.toLowerCase()) {
            case "wnacg" -> {
                // 針對 wnacg 網站，抓取 aid 編號與標題
                normalCase = Optional.ofNullable(Wnacg_Number(url));
            }
            case "18comic" -> {
                // 針對 18comic 網站，抓取 album 編號與 og:title
                normalCase = Optional.ofNullable(comic_Number(url));
            }
            case "nhentai" -> {
                // 針對 nhentai 網站，抓取 g/編號，標題沿用原本解析結果
                Integer number = Nhentai_Number(url);
                if (number != null) {
                    normalCase = Optional.of(new Pair<>(number, newLink.getName()));
                }
            }
            // 預設其他網站，不做任何處理
        }

        // ⭐ 將解析結果套用到 newLink
        if (normalCase.isPresent()) {
            // 有解析結果時，設定編號和標題
            newLink.setNumber(normalCase.get().getKey()); // 設定編號
            newLink.setName(normalCase.get().getValue()); // 設定標題
        } else {
            // 其他未支援類型，直接將 URL 當作名稱
            newLink.setName(url);
        }
    }
}
