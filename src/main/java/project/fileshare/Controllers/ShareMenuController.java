// Almost everything in this class references info from the table view in the dashboard controller.

package project.fileshare.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

import static project.fileshare.Controllers.SceneManager.userIdForManager;
import static project.fileshare.JDBC.FilesShareDAO.*;

public class ShareMenuController {

    @FXML
    public TextField shareWithField;

    public static DashboardController.FileEntry file;
    private Stage dialogStage;

    // Sets the file as the file from the dashboard controller that was right-clicked to be shared.

    public void setFile(DashboardController.FileEntry file) {
        ShareMenuController.file = file;
    }

    // Sets the dialog stage so the window can be closed after sharing.

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    // Tries to share the file that was selected by calling the shareFile method from the
    // FilesShareDAO class. Throws SQLExceptions because of the shareFile method.

    @FXML
    public void handleShareButton(ActionEvent actionEvent) throws SQLException {
        try {
            shareFile(getFileID(file.getFileName(), userIdForManager), getIdFromUsername(shareWithField.getText()), "read", userIdForManager);
        } catch (SQLException e) {
            System.out.println("Couldn't share file: " + e);
            throw new RuntimeException(e);
        }
    }
}