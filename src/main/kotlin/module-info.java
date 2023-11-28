module com.example.cheat_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;

    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    opens com.example.cheat_project to javafx.fxml;
    exports com.example.cheat_project;
}