package com.example.linker.OtherFunction;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * ChooseBrowser
 * 專責管理本地端瀏覽器偵測、選擇與開啟網址功能（支援自動與手動設定路徑）
 */
public class ChooseBrowser {

    // ⭐ 目前使用者選擇的瀏覽器，預設為系統瀏覽器
    private static String currentBrowser = "System Default";

    /**
     * 掃描本地端已安裝的瀏覽器清單
     * 優先查 Registry，查不到再查常見的安裝路徑
     * @return 已安裝的瀏覽器名稱列表
     */
    public static List<String> AddBrowers() {
        List<String> browsers = new ArrayList<>();

        // 所有支援的瀏覽器名稱列表
        String[] browserNames = {"Chrome", "Edge", "Firefox", "Brave", "Opera"};

        for (String name : browserNames) {
            String exeName;

            // 使用 switch 對應各瀏覽器的 exe 檔案名稱
            switch (name) {
                case "Chrome" -> exeName = "chrome.exe";
                case "Edge" -> exeName = "msedge.exe";
                case "Firefox" -> exeName = "firefox.exe";
                case "Brave" -> exeName = "brave.exe";
                case "Opera" -> exeName = "launcher.exe";
                default -> {
                    System.out.println("❌ 未知瀏覽器名稱：" + name);
                    continue;
                }
            }

            // 檢查該瀏覽器是否存在
            if (checkRegistry(exeName)) {
                System.out.println("✅ " + name + " 存在");
                browsers.add(name);
            } else {
                System.out.println("❌ " + name + " 不存在");
            }
        }

        // 固定加入系統預設
        browsers.add("System Default");
        return browsers;
    }



    /**
     * 設定目前選擇的瀏覽器
     * @param browser 使用者從下拉式選單選擇的瀏覽器名稱
     */
    public static void setCurrentBrowser(String browser) {
        currentBrowser = browser;
    }

    /**
     * 取得目前設定的瀏覽器
     * @return 瀏覽器名稱字串
     */
    public static String getCurrentBrowser() {
        return currentBrowser;
    }

    /**
     * 根據目前選擇的瀏覽器開啟指定網址
     * @param url 要打開的網站連結
     */
    public static void choose(String url) {
        try {
            if (currentBrowser.equals("System Default")) {
                System.out.println("➡️ 開啟網址時使用的瀏覽器：" + currentBrowser);
                java.awt.Desktop.getDesktop().browse(new URI(url));
            } else {
                String browserPath;

                // ⭐ 如果是完整路徑，就直接用，不再經過 getBrowserPath()
                File customFile = new File(currentBrowser);
                if (customFile.exists() && customFile.isFile()) {
                    browserPath = currentBrowser;
                } else {
                    browserPath = getBrowserPath(currentBrowser); // Chrome, Edge, 等
                    System.out.println(new File("C:\\Program Files\\Firefox\\firefox.exe").exists());
                }

                if (browserPath != null) {
                    new ProcessBuilder(browserPath, url).start();
                } else {
                    System.out.println("❌ 找不到瀏覽器路徑，改用系統預設！");
                    java.awt.Desktop.getDesktop().browse(new URI(url));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                java.awt.Desktop.getDesktop().browse(new URI(url));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 根據瀏覽器名稱找出其可執行檔路徑
     * 優先從 Registry 抓，找不到再手動設常見安裝路徑
     * @param browserName 瀏覽器名稱（例如 Chrome）
     * @return 瀏覽器執行檔完整路徑
     */
    public static String getBrowserPath(String browserName) {
        String exeName = switch (browserName) {
            case "Chrome" -> "chrome.exe";
            case "Edge" -> "msedge.exe";
            case "Firefox" -> "firefox.exe";
            case "Brave" -> "brave.exe";
            case "Opera" -> "launcher.exe";
            default -> null;
        };
        if (exeName == null) return null;

        // 先從 Registry 查詢
        String fromRegistry = getPathFromRegistry(exeName);
        if (fromRegistry != null) return fromRegistry;

        // Registry 沒有的話，用自定義安裝路徑查找
        return MantleSetBrowsers(browserName);
    }

    /**
     * 從 Windows Registry 中查詢瀏覽器路徑（使用 reg 指令）
     * @param exeName 瀏覽器執行檔名稱
     * @return 查到的路徑或 null（查不到）
     */
    private static String getPathFromRegistry(String exeName) {
        try {
            // 組出查詢語法
            String command = "reg query \"HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\App Paths\\" + exeName + "\" /ve";
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("(Default)")) {
                    // 格式為 (Default)    REG_SZ    C:\完整路徑
                    String[] parts = line.split("\\s+");
                    if (parts.length >= 3) {
                        return parts[2];
                    }
                }
            }
            reader.close();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 自定義常見安裝路徑（當 Registry 查不到時的後備方案）
     * @param browserName 瀏覽器名稱（例如 Chrome）
     * @return 找到的完整路徑或 null
     */
    private static String MantleSetBrowsers(String browserName) {
        return switch (browserName) {
            case "Chrome" -> {
                String path = "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe";
                yield new File(path).exists() ? path : null;
            }
            case "Edge" -> {
                String path = "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe";
                yield new File(path).exists() ? path : null;
            }
            case "Firefox" -> {
                String path = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
                yield new File(path).exists() ? path : null;
            }
            case "Brave" -> {
                String path = "C:\\Program Files\\BraveSoftware\\Brave-Browser\\Application\\brave.exe";
                yield new File(path).exists() ? path : null;
            }
            case "Opera" -> {
                String path = "C:\\Program Files\\Opera\\launcher.exe";
                yield new File(path).exists() ? path : null;
            }
            default -> null;
        };
    }

    /**
     * 檢查某個瀏覽器是否存在於系統（Registry 或常見路徑）
     * @param exeName 瀏覽器的執行檔名稱（例如 chrome.exe）
     * @return true 表示存在，false 表示找不到
     */
    private static boolean checkRegistry(String exeName) {
        // ⭐ exeName 是 xxx.exe，要轉為名稱
        String browserName = exeName.replace(".exe", "");
        return getPathFromRegistry(exeName) != null || MantleSetBrowsers(browserName) != null;
    }

}
