package project.fileshare.Controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {
    private static SceneManager instance;
    private Stage primaryStage;

    private SceneManager() {}

    public static SceneManager getInstance(){
        if (instance == null){
            instance = new SceneManager();
        }
        return instance;
    }

    public void setPrimaryStage(Stage stage){
        this.primaryStage = stage;
    }

    public void switchToSignup() {
        switchToScene("/project/fileshare/sign-up.fxml");
    }

    public void switchToLoginPage() {
        switchToScene("/project/fileshare/login-page.fxml");
    }

    public void switchToDashboard() {
        switchToScene("/project/fileshare/dashboard.fxml");
    }

    public void switchToScene(String fxmlFile){
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Scene newScene = new Scene(root);
            primaryStage.setScene(newScene);
        } catch (IOException e) {
            System.out.println("failed to load: "+ fxmlFile);
            e.printStackTrace();
        }

    }

}
