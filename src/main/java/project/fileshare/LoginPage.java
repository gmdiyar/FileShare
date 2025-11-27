package project.fileshare;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginPage extends Application {
    @Override
    public void start(Stage stage) throws IOException {
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
