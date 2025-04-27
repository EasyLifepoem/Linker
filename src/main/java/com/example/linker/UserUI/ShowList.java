package com.example.linker.UserUI;

// 匯入 JavaFX 所需的類別
import com.example.linker.HelloApplication;
import com.example.linker.LineModel;
import com.example.linker.YamlService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
// ShowList 是這個畫面的控制器，會對應到 FXML 中的 fx:controller
public class ShowList  {

    @FXML
    private Text Updated_Text;

    // 透過 fx:id="listView" 綁定 FXML 中的 ListView 元件
    @FXML
    private ListView<String> listView;

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

        if (model == null || model.getWebType() == null) {
            listView.getItems().add("找不到資料！");
            return;
        }

        listView.getItems().setAll( // ⭐ setAll會清空舊資料並加入新資料
                model.getWebType().stream()
                        .filter(webType -> webType.getLinks() != null)
                        .flatMap(webType -> webType.getLinks().stream()
                                .map(link -> formatDisplayText(webType.getType(), link))
                        )
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
            //close this windows
            stage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void Delete_Selected() {
        String selectedItem = listView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            System.out.println("沒有選取任何項目");
            return;
        }

        try {
            // 解析 selectedItem 拿出 type, name, url
            int typeStart = selectedItem.indexOf('[');
            int typeEnd = selectedItem.indexOf(']');
            int dashIndex = selectedItem.lastIndexOf(" - ");

            if (typeStart == -1 || typeEnd == -1 || dashIndex == -1) {
                System.out.println("格式錯誤，無法解析");
                return;
            }

            String type = selectedItem.substring(typeStart + 1, typeEnd).trim();
            String name = selectedItem.substring(typeEnd + 1, dashIndex).trim();
            String url = selectedItem.substring(dashIndex + 3).trim();

            LineModel model = HelloApplication.Global_LineModel;
            if (model == null || model.getWebType() == null) {
                System.out.println("資料模型錯誤！");
                return;
            }

            // 找到對應 WebType
            LineModel.WebType targetWebType = model.getWebType().stream()
                    .filter(webType -> type.equals(webType.getType()))
                    .findFirst()
                    .orElse(null);

            if (targetWebType == null) {
                System.out.println("找不到對應網站分類！");
                return;
            }

            // 找到對應 Link
            LineModel.LinkEntry targetLink = targetWebType.getLinks().stream()
                    .filter(link -> name.equals(link.getName()) && url.equals(link.getURL()))
                    .findFirst()
                    .orElse(null);

            if (targetLink == null) {
                System.out.println("找不到對應連結！");
                return;
            }

            // 刪除 Link
            targetWebType.getLinks().remove(targetLink);
            Updated_Text.setText("成功刪除連結");

            // 如果該 WebType 沒有任何 link 了，順便移除整個 type
            if (targetWebType.getLinks().isEmpty()) {
                model.getWebType().remove(targetWebType);
                System.out.println("該網站分類已空，已刪除分類！");
            }

            // 直接從 ListView 上移除被選取的項目
            listView.getItems().remove(selectedItem);

            // 更新保存到 YAML
            YamlService.writeYaml(model);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
