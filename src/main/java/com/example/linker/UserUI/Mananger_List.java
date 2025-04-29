package com.example.linker.UserUI;

import com.example.linker.HelloApplication;
import com.example.linker.LineModel;
import com.example.linker.OtherFunction.ChooseBrowser;
import com.example.linker.YamlService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.io.File;
import java.util.List;

public class Mananger_List {

    @FXML
    private ComboBox<String> browserComboBox;

    @FXML
    private TextField customPathField;

    @FXML
    private void initialize() {
        // Step 1: 載入所有系統偵測瀏覽器
        List<String> availableBrowsers = ChooseBrowser.AddBrowers();
        if (availableBrowsers.isEmpty()) {
            showAlert("找不到任何瀏覽器！");
            return;
        }

        browserComboBox.getItems().addAll(availableBrowsers);

        // Step 2: 載入 YAML 中的 CustomBrowsers
        LineModel model = HelloApplication.Global_LineModel;
        if (model != null && model.getCustomBrowserPaths() != null) {
            for (String custom : model.getCustomBrowserPaths()) {
                if (!browserComboBox.getItems().contains(custom)) {
                    browserComboBox.getItems().add(custom);
                }
            }
        }

        // Step 3: 設定選擇的預設瀏覽器
        if (model != null && model.getSelectedBrowser() != null) {
            String saved = model.getSelectedBrowser();
            if (!browserComboBox.getItems().contains(saved)) {
                browserComboBox.getItems().add(saved);
            }
            browserComboBox.getSelectionModel().select(saved);
            ChooseBrowser.setCurrentBrowser(saved);
        } else {
            String first = browserComboBox.getItems().get(0);
            browserComboBox.getSelectionModel().select(first);
            ChooseBrowser.setCurrentBrowser(first);
            if (model != null) {
                model.setSelectedBrowser(first);
                YamlService.writeYaml(model);
            }
        }

        // Step 4: 切換選單時更新 YAML 與目前瀏覽器（修正版）
        browserComboBox.setOnAction(event -> {
            String selectedBrowser = browserComboBox.getSelectionModel().getSelectedItem();
            if (selectedBrowser == null || selectedBrowser.isEmpty()) return;

            ChooseBrowser.setCurrentBrowser(selectedBrowser);

            if (model != null) {
                String finalSave;
                if (new File(selectedBrowser).exists()) {
                    finalSave = selectedBrowser;
                } else {
                    String resolved = ChooseBrowser.getBrowserPath(selectedBrowser);
                    finalSave = (resolved != null) ? resolved : "System Default";
                }

                model.setSelectedBrowser(finalSave);
                YamlService.writeYaml(model);

                System.out.println("✅ 實際保存的瀏覽器路徑：" + finalSave);
            }
        });
    }

    @FXML
    private void MantleAddBrowser() {
        String customPath = customPathField.getText().trim();
        if (!customPath.toLowerCase().endsWith(".exe")) {
            showAlert("請輸入正確的執行檔路徑（*.exe）！");
            return;
        }

        if (customPath.isEmpty()) {
            showAlert("請輸入有效的瀏覽器路徑！");
            return;
        }

        File file = new File(customPath);
        if (!file.exists() || !file.isFile()) {
            showAlert("找不到指定的執行檔！");
            return;
        }

        // 若不在列表中，加入選單
        if (!browserComboBox.getItems().contains(customPath)) {
            browserComboBox.getItems().add(customPath);
        }

        // 選取此瀏覽器並記錄
        browserComboBox.getSelectionModel().select(customPath);
        ChooseBrowser.setCurrentBrowser(customPath);

        LineModel model = HelloApplication.Global_LineModel;
        if (model != null) {
            model.setSelectedBrowser(customPath);

            // 加入 YAML 中的 CustomBrowsers 清單
            List<String> customList = model.getCustomBrowserPaths();
            if (!customList.contains(customPath)) {
                customList.add(customPath);
            }

            YamlService.writeYaml(model);
            System.out.println("✅ 已新增自訂瀏覽器並保存：" + customPath);
        } else {
            showAlert("全域資料模型錯誤！");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onManageList() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("管理列表");
        alert.setHeaderText(null);
        alert.setContentText("這是管理列表的功能！");
        alert.showAndWait();
    }
}
