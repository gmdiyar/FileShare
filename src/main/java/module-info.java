module project.fileshare {
    requires javafx.controls;
    requires javafx.fxml;


    opens project.fileshare to javafx.fxml;
    exports project.fileshare;
}