package project.fileshare.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static project.fileshare.Controllers.SceneManager.userIdForManager;
import static project.fileshare.JDBC.FilesShareDAO.*;

public class ShareMenuController {

    @FXML
    public TextField shareWithField;
    public static DashboardController.FileEntry file;
    private Stage dialogStage;

    public void setFile(DashboardController.FileEntry file) {
        ShareMenuController.file = file;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }


    @FXML
    public void handleShareButton(ActionEvent actionEvent) throws SQLException {
        System.out.println(file.getFileName());
        System.out.println(shareWithField.getText());
        System.out.println(getIdFromUsername(shareWithField.getText()));
        try {
            shareFile(getFileID(file.getFileName(), userIdForManager), getIdFromUsername(shareWithField.getText()), "read", userIdForManager);
        } catch (SQLException e) {
            System.out.println("couldnt share file: " + e);
            throw new RuntimeException(e);
        }
    }
}
