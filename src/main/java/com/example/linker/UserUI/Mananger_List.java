package com.example.linker.UserUI;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Mananger_List {

    @FXML
    private void initialize() {
        // 可以做初始列表載入
    }

    @FXML
    private void onManageList() {
        // 實作管理列表的邏輯
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("管理列表");
        alert.setHeaderText(null);
        alert.setContentText("這是管理列表的功能！");
        alert.showAndWait();
    }
}
