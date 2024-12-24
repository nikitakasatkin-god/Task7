module com.example.task7 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.task7 to javafx.fxml;
    exports com.example.task7;
}