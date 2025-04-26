package com.example.linker;

import com.example.linker.LineModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

/**
 * YamlService 類別
 * 負責控制 NoteList.yml 的讀取與寫入
 */
public class YamlService {
    // 建立一個 ObjectMapper，使用 YAML 格式處理資料
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    /**
     * 從 classpath 中讀取 NoteList.yml
     * @return 讀取成功則回傳 LineModel，失敗則回傳空物件或 null
     */
    public static LineModel readYaml() {
        /** YamlService.class.getResourceAsStream("/NoteList.yaml")
         * 找相對路徑下的NoteList.yml 存進input
         * 重要的一點是：使用 try-with-resources 自動關閉 InputStream
         */
        try (InputStream input = YamlService.class.getResourceAsStream("/NoteList.yaml")) {
            // 如果找不到 YAML 檔案
            if (input == null) {
                System.out.println("找不到 NoteList.yml");
                return new LineModel();// 回傳一個新的空白 LineModel 物件，防止錯誤
            }
            // 使用 ObjectMapper 把 YAML 解析成 LineModel 類別
            return mapper.readValue(input, LineModel.class);
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
    public static void writeYaml(LineModel model) {
        try {
            // 取得 classpath 中 NoteList.yml 的 URL
            URL resourceUrl = YamlService.class.getResource("/NoteList.yml");
            if (resourceUrl == null) {
                System.err.println("無法寫入：找不到 NoteList.yaml 實體路徑");
                return;
            }
            // 把 URL 轉成實體檔案路徑（Path 物件）
            var path = Paths.get(resourceUrl.toURI());
            // 使用 ObjectMapper 把 LineModel 轉成 YAML 檔案寫回去
            mapper.writeValue(path.toFile(), model);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
