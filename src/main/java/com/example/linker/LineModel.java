package com.example.linker;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * LineModel 整合版
 * 同時包含:
 * - 群組名稱 (NAME)
 * - 分類類型 (type)
 * - 連結清單 (name + url)
 */

@JsonIgnoreProperties(ignoreUnknown = true)//忽略 JSON / YAML 檔案中多餘的欄位
public class LineModel {
    @JsonProperty("NAME")//明確指定 Java 欄位（或方法）對應到 JSON 或 YAML 中的NAME名稱。
    private String NAME; // 群組名稱 (例如：用戶)
    @JsonProperty("WebType")
    private List<WebType> WebType; // 每個群組下面的網站分類列表
    @JsonProperty("SelectedBrowser")
    private String SelectedBrowser; // ⭐ 使用者選擇的瀏覽器名稱
    @JsonProperty("CustomBrowsers")
    private List<String> customBrowserPaths = new ArrayList<>();

    public LineModel() {}

    //有傳參數的建構子
    public LineModel(String NAME, List<WebType> WebType) {
        this.NAME = NAME;
        this.WebType = WebType;
    }
    @JsonProperty("NAME")
    public String getNAME() {return NAME;}
    @JsonProperty("NAME")
    public void setNAME(String NAME) {this.NAME = NAME;}
    @JsonProperty("WebType")
    public List<WebType> getWebType() {return WebType;}
    @JsonProperty("WebType")
    public void setWebType(List<WebType> WebType) {this.WebType = WebType;}
    @JsonProperty("SelectedBrowser")
    public String getSelectedBrowser() {return SelectedBrowser;}

    @JsonProperty("SelectedBrowser")
    public void setSelectedBrowser(String selectedBrowser) {SelectedBrowser = selectedBrowser;}
    @JsonProperty("CustomBrowsers")
    public List<String> getCustomBrowserPaths() {return customBrowserPaths;}

    @JsonProperty("CustomBrowsers")
    public void setCustomBrowserPaths(List<String> customBrowserPaths) {this.customBrowserPaths = customBrowserPaths;}
    /**
     * 內部類：WebType
     * 每個分類型下有多個連結
     */
    //static是為了類別使用
    public static class WebType {
        private String type; // 分類 (例如 hentai, Wacg)
        private List<LinkEntry> links; // 對應的連結清單

        public WebType() {}

        //有傳建構子
        public WebType(String type, List<LinkEntry> links) {
            this.type = type;
            this.links = links;
        }

        public String getType() {return type;}

        public void setType(String type) {this.type = type;}

        public List<LinkEntry> getLinks() {return links;}

        public void setLinks(List<LinkEntry> links) {this.links = links;}
    }

    /**
     * 內部類：LinkEntry
     * 每一個實際的連結資料
    *     links:
    *         - name: 內容1
    *         URL: https://example.com/wacg1
    *         - name: 內容2
    *         URL: https://example.com/wacg2
     */
    public static class LinkEntry {
        private String name; // 顯示名稱
        private String URL;  // 網址
        //放號碼
        private Integer Number;

        public LinkEntry() {}

        //有傳建構子
        public LinkEntry(String name, String URL) {
            this.name = name;
            this.URL = URL;
        }
        @JsonProperty("name")
        public String getName() {return name;}
        @JsonProperty("name")
        public void setName(String name) {this.name = name;}
        @JsonProperty("URL")
        public String getURL() {return URL;}
        @JsonProperty("URL")
        public void setURL(String URL) {this.URL = URL;}
        @JsonProperty("Number")
        public Integer getNumber() { return Number; }
        @JsonProperty("Number")
        public void setNumber(Integer number) { this.Number = number; }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            sb.append(this.getURL() != null ? this.getURL().split("/")[2] : "未知"); // 從 URL 拿 domain 來當分類
            sb.append("]");
            if (this.getNumber() != null) {
                sb.append("[").append(this.getNumber()).append("]");
            }
            sb.append(" - ").append(this.getName() != null ? this.getName() : "未命名");
            return sb.toString();
        }

    }
}
