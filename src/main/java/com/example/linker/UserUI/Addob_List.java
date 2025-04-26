package com.example.linker.UserUI;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;

public class Addob_List {
    @FXML
    private TextArea linkInput;
    @FXML
    private void initialize() {
        // 初始化區塊，如果有需要可以在這裡設定元件或初始值
    }

    @FXML
    private void onAddLink() {
        String userInput = linkInput.getText();
        System.out.println("輸入內容：" + userInput);
    }
}
