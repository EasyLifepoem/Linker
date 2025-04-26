package com.example.linker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        //建立一個Parent物件，名稱為root,Parent 是 JavaFX 中所有容器型元件（layout）的共同父類別
        Parent root = fxmlLoader.load();
        //root取自hello-view.fxml中設定，長800高600
        Scene scene = new Scene(root,800,600   );
        stage.setTitle("快捷記事本");
        stage.setScene(scene);
        //可以調整窗口大小
        stage.setResizable(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}