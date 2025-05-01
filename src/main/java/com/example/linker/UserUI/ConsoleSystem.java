package com.example.linker.UserUI;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * ConsoleSystem
 * 將 System.out/System.err 重導向到 JavaFX 的 TextArea。
 */
public class ConsoleSystem {

    /**
     * 將系統輸出導向到指定的 TextArea（支援多執行緒）
     *
     * @param textArea 目標 JavaFX TextArea
     */
    public static void bindTo(TextArea textArea) {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) {
                Platform.runLater(() -> textArea.appendText(String.valueOf((char) b)));
            }

            @Override
            public void write(byte[] b, int off, int len) {
                String text = new String(b, off, len);
                Platform.runLater(() -> textArea.appendText(text));
            }
        };

        // 將 System.out / System.err 重導向
        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }
}
