module com.example.java_minigame {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.java_minigame to javafx.fxml;
    exports com.example.java_minigame;
}