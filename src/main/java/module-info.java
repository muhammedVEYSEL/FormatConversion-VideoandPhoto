module com.example.changeformat {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens com.example.changeformat to javafx.fxml;
    exports com.example.changeformat;
}