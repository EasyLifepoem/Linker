package com.example.linker;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void Show_List() throws IOException {
        openNewWindow("show-list.fxml", "展示列表");
    }

    @FXML
    protected void Addob_List() throws IOException {
        openNewWindow("Addob_List.fxml", "添加鏈接");
    }
    @FXML
    protected void Mananger_List() throws IOException {
        openNewWindow("Manager_List.fxml", "管理列表");
    }

    // 工具方法：開啟新視窗
    private void openNewWindow(String fxmlFile, String title) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.show();
    }

}
