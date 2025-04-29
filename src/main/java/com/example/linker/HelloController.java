package com.example.linker;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    // 開啟展示列表頁面
    @FXML
    protected void Show_List() throws IOException {
        openNewWindow("show-list.fxml", "展示列表");
    }

    // 開啟添加鏈接頁面
    @FXML
    protected void Addob_List() throws IOException {
        openNewWindow("Addob_List.fxml", "添加鏈接");
    }
    // 開啟管理瀏覽器頁面
    @FXML
    protected void Mananger_List() throws IOException {
        openNewWindow("Manager_List.fxml", "管理列表");
    }

    // 工具方法：開啟新視窗
    private void openNewWindow(String fxmlFile, String title) throws IOException {
        //建立一個fxmlLoader來取得當前class底下的Resource中的fxmlFile
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
        //建立一個名為root的Parent物件,parent物件是fxml的最上面的那個method
        Parent root = fxmlLoader.load();
        //建立一個stage,stage就是視窗
        Stage stage = new Stage();
        stage.setTitle(title);
        //把這個stage以root讀到fxml格式打開
        stage.setScene(new Scene(root));
        stage.show();// 顯示新開視窗
    }

}
