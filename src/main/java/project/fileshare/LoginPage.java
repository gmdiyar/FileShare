// This class sets the window up, launches it, and switches to the login-page.fxml view.
// As apart of the set up, it sets this JavaFX stage as the primary stage in the scene manager class. 
// If it fails, it'll throw a runtime exception and print a fail line to console.

package project.fileshare;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import project.fileshare.Controllers.SceneManager;

import java.io.IOException;

public class LoginPage extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        SceneManager.getInstance().setPrimaryStage(stage);

        try {
            Parent root = FXMLLoader.load(getClass().getResource("login-page.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("FileShare");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Failed to load main window.");
            throw new RuntimeException(e);
        }
    }
}
