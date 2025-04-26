module com.example.linker {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires java.desktop;

    opens com.example.linker to javafx.fxml;
    //新增UserUI的路徑
    opens com.example.linker.UserUI to javafx.fxml;

    exports com.example.linker;
    //新增UserUI的路徑
    exports com.example.linker.UserUI;
}