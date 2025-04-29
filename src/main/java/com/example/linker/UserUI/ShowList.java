package com.example.linker.UserUI;

// 匯入 JavaFX 所需的類別
import com.example.linker.HelloApplication;
import com.example.linker.LineModel;
import com.example.linker.OtherFunction.ChooseBrowser;
import com.example.linker.YamlService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;

// ShowList 是這個畫面的控制器，會對應到 FXML 中的 fx:controller
public class ShowList  {

    @FXML
    private Text Updated_Text;

    // 透過 fx:id="listView" 綁定 FXML 中的 ListView 元件
    @FXML
    private ListView<LineModel.LinkEntry> listView;


    // 透過 fx:id="backButton" 綁定 FXML 中的回主畫面按鈕
    @FXML
    private Button Returnmain_Button;
    @FXML
    private Button Delete_Button;


    @FXML
    public void initialize() {
        loadYamlData();  // 初始化時呼叫載入資料
        List_ClickHandler(); // 設定 ListView 點擊打開網址功能
    }

    /**
     * ⭐ 載入 YAML 資料並填充到 ListView
     */
    private void loadYamlData() {
        LineModel model = HelloApplication.Global_LineModel;
        ChooseBrowser.setCurrentBrowser(model.getSelectedBrowser());

        if (model == null || model.getWebType() == null) {
            listView.getItems().add(new LineModel.LinkEntry("找不到資料！", ""));
            return;
        }

        listView.getItems().setAll(
                model.getWebType().stream()
                        .filter(webType -> webType.getLinks() != null)
                        .flatMap(webType -> webType.getLinks().stream())
                        .toList()
        );
    }


    /**
     * ⭐ 格式化每一條要顯示在 ListView 的字串
     * 格式：[類型][編號] - 名稱
     */
    private String formatDisplayText(String type, LineModel.LinkEntry link) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(type).append("]");
        if (link.getNumber() != null) {
            sb.append("[").append(link.getNumber()).append("]");
        }
        sb.append(" - ").append(link.getName());
        return sb.toString();
    }
    /**
     * ⭐ 專門負責設定 ListView 的點擊開啟網址功能
     */
    private void List_ClickHandler() {
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                LineModel.LinkEntry selectedItem = listView.getSelectionModel().getSelectedItem();
                if (selectedItem != null && selectedItem.getURL() != null) {
                    openUrlFromSelectedItem(selectedItem);
                }
            }
        });

    }

    /**
     * ⭐ 從被選取的項目中解析出 URL 並打開它
     * @param selectedItem ListView 中選取的文字
     */

    // ⭐ 點擊連結時開啟網址
    private void openUrlFromSelectedItem(LineModel.LinkEntry selectedItem) {
        try {
            String url = selectedItem.getURL().trim();
            if (!url.startsWith("http")) {
                System.out.println("無效網址：" + url);
                return;
            }

            // ⭐ 讀取 YAML 中的瀏覽器設定，並設定到 ChooseBrowser
            LineModel model = HelloApplication.Global_LineModel;
            if (model != null && model.getSelectedBrowser() != null) {
                ChooseBrowser.setCurrentBrowser(model.getSelectedBrowser());
            } else {
                ChooseBrowser.setCurrentBrowser("System Default");
            }

            // 🧭 用目前選擇的瀏覽器開啟網址
            ChooseBrowser.choose(url);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 從 ListView 中的顯示文字中提取網址
     * 規則：
     * - 如果是 [wnacg][編號]，自動組成 wnacg 專屬網址
     * - 如果是 [18comic][編號]，自動組成 18comic 專屬網址
     * - 其他情況，取 " - " 後面的原生網址
     *
     * @param text ListView 顯示的完整字串
     * @return 提取出來的網址
     */
    private String extractUrl(String text) {
        // 找第一個 ] 和第二個 [ ] 的位置
        int firstBracketEnd = text.indexOf("]");
        int secondBracketStart = text.indexOf("[", firstBracketEnd);
        int secondBracketEnd = text.indexOf("]", secondBracketStart);

        // 如果格式錯誤，直接取 " - " 後面的網址
        if (firstBracketEnd == -1 || secondBracketStart == -1 || secondBracketEnd == -1) {
            int dashIndex = text.lastIndexOf(" - ");
            if (dashIndex != -1 && dashIndex + 3 < text.length()) {
                return text.substring(dashIndex + 3);
            }
            return text;
        }

        // 擷取類型 (type) 與編號 (number)
        String type = text.substring(1, firstBracketEnd).toLowerCase(); // 取第一個 [] 中的內容
        if (type.startsWith("www.")) {
            type = type.substring(4); // 如果開頭是 www.，去掉
        }
        String number = text.substring(secondBracketStart + 1, secondBracketEnd); // 取第二個 [] 中的內容

        // 用 switch-case 判斷
        switch (type) {
            case "wnacg":
                // 專門組 wnacg 網址
                return "https://www.wnacg.com/photos-index-aid-" + number + ".html";
            case "18comic":
                // 專門組 18comic 網址
                return "https://www.18comic.vip/album/" + number;
            default: {
                // 其他網站類型，直接取 " - " 後面的網址
                int dashIndex = text.lastIndexOf(" - ");
                if (dashIndex != -1 && dashIndex + 3 < text.length()) {
                    return text.substring(dashIndex + 3);
                }
                return text;
            }
        }
    }

    @FXML
    private  void BackMain() {
        try {
            // 讀取主畫面的 FXML 檔案
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/linker/hello-view.fxml"));
            Parent root = loader.load();
            // 取得目前按鈕所在的 Stage
            Stage stage = (Stage) Returnmain_Button.getScene().getWindow();
            // 將 Stage 的 Scene 換成主畫面的內容
            stage.getScene().setRoot(root);
            stage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    private void Delete_Selected() {
        LineModel.LinkEntry selectedItem = listView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            System.out.println("沒有選取任何項目");
            return;
        }

        try {
            LineModel model = HelloApplication.Global_LineModel;
            if (model == null || model.getWebType() == null) {
                System.out.println("資料模型錯誤！");
                return;
            }

            for (LineModel.WebType webType : model.getWebType()) {
                if (webType.getLinks().remove(selectedItem)) {
                    System.out.println("成功刪除連結！");
                    listView.getItems().remove(selectedItem);
                    Updated_Text.setText("成功刪除連結！");
                    if (webType.getLinks().isEmpty()) {
                        model.getWebType().remove(webType);
                        System.out.println("該網站分類已空，已刪除分類！");
                    }
                    YamlService.writeYaml(model);
                    return;
                }
            }

            System.out.println("找不到對應連結！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
