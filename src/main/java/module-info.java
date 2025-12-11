module project.fileshare {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires javafx.graphics;

    opens project.fileshare to javafx.fxml;
    opens project.fileshare.JDBC;
    opens project.fileshare.Controllers to javafx.fxml;

    exports project.fileshare.Controllers to javafx.fxml;
    exports project.fileshare;
}