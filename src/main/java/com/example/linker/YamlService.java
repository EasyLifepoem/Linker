package com.example.linker;

import com.example.linker.LineModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import javafx.scene.control.Alert;

import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * YamlService 類別
 * 負責控制 NoteList.yml 的讀取與寫入
 */
public class YamlService {
    // 建立一個 ObjectMapper，使用 YAML 格式處理資料
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    // 外部實體檔案路徑（在 jar 同層）
    private static final Path externalYamlPath = Paths.get("NoteList.yaml");
    /**
     * 從 classpath 中讀取 NoteList.yml
     * @return 讀取成功則回傳 LineModel，失敗則回傳空物件或 null
     */
    public static LineModel readYaml() {
        /** YamlService.class.getResourceAsStream("/NoteList.yaml")
         * 找相對路徑下的NoteList.yml 存進input
         * 重要的一點是：使用 try-with-resources 自動關閉 InputStream
         */
        try {
            // 1. 檢查外部 YAML 是否存在
            if (!Files.exists(externalYamlPath)) {
                // 如果不存在，自動複製 classpath 裡面的範本
                copyYamlFromClasspath();
            }
            // 2. 使用外部檔案載入
            return mapper.readValue(externalYamlPath.toFile(), LineModel.class);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 儲存 LineModel 到 YAML
     * 注意：寫入需要實體檔案路徑，JAR 執行時不能寫進 classpath 裡
     * @param model 要寫入的 LineModel 資料
     */
    /**
     * 寫入 YAML：只寫外部檔案
     */
    public static void writeYaml(LineModel model) {
        if (model == null) {
            showError("保存失敗：資料為空！");
            return;
        }
        try {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(externalYamlPath.toFile(), model);
            System.out.println("資料已成功保存到 NoteList.yaml！");
        } catch (IOException e) {
            e.printStackTrace();
            showError("保存 YAML 失敗！");
        }
    }
    /**
     * 若外部 YAML 檔不存在，從 classpath 複製出初始範本
     */
    private static void copyYamlFromClasspath() {
        try (InputStream input = YamlService.class.getResourceAsStream("/NoteList.yaml")) {
            if (input == null) {
                throw new IOException("找不到內建 NoteList.yaml！");
            }
            Files.copy(input, externalYamlPath);
            System.out.println("已從範本複製 NoteList.yaml 到外部目錄！");
        } catch (IOException e) {
            e.printStackTrace();
            showError("初始化 NoteList.yaml 失敗！");
        }
    }

    /**
     * 顯示錯誤提示視窗
     */
    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("錯誤");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
