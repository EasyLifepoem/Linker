package com.example.linker.UserUI;

// 匯入 JavaFX 所需的類別
import com.example.linker.LineModel;
import com.example.linker.YamlService;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import java.awt.Desktop;
import java.net.URI;
// ShowList 是這個畫面的控制器，會對應到 FXML 中的 fx:controller
public class ShowList  {

    // 透過 fx:id="listView" 綁定 FXML 中的 ListView 元件
    @FXML
    private ListView<String> listView;

    @FXML
    public void initialize() {
        // 讀取 YAML 資料
        LineModel model = YamlService.readYaml();
        //如果model不等於null或者 model中的webtypev不等於null
        if (model != null && model.getWebType() != null) {
            // 把 WebType 資料流式處理，減少巢狀 for 迴圈
            listView.getItems().addAll(
                    model.getWebType().stream()// 將 WebType List 轉成 Stream
                            .filter(webType -> webType.getLinks() != null)// 過濾掉 links 為 null 的 WebType
                            .flatMap(webType -> webType.getLinks().stream()// 將每個 WebType 的 links 也轉成 Stream
                                    .map(link -> "[" + webType.getType() + "] " + link.getName() + " - " + link.getURL())
                            )// 把每個 link 轉成要顯示在 ListView 的格式
                            .toList() // Java 16+可以用 toList()，如果是 Java 8-11 換成 collect(Collectors.toList())
            );
        } else {
            listView.getItems().add("找不到資料！");
        }
        List_ClickHandler();
    }
    /**
     * ⭐ 專門負責設定 ListView 的點擊開啟網址功能
     */
    private void List_ClickHandler() {
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // 兩下點擊觸發
                String selectedItem = listView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    openUrlFromSelectedItem(selectedItem);
                }
            }
        });
    }

    /**
     * ⭐ 從被選取的項目中解析出 URL 並打開它
     * @param selectedItem ListView 中選取的文字
     */
    private void openUrlFromSelectedItem(String selectedItem) {
        try {
            String url = extractUrl(selectedItem);// 擷取 URL
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));// 呼叫系統預設瀏覽器開啟
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 輔助方法：從 "[類型] 名稱 - URL" 的格式中提取 URL
     * @param text 顯示在 ListView 的完整字串
     * @return 擷取出的 URL 字串
     */
    private String extractUrl(String text) {
        //從讀取的文字中找出 URL 的位置
        int dashIndex = text.lastIndexOf(" - ");
        //判斷不會超過長度
        if (dashIndex != -1 && dashIndex + 3 < text.length()) {
            // 從 " - " 之後3格的位置取出 URL
            return text.substring(dashIndex + 3);
        }
        return text; // 萬一格式錯了，直接拿整串
    }

}
