package project.fileshare.Controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class SceneManager {

    public static int userIdForManager = 0;

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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/project/fileshare/dashboard.fxml"));
            Parent root = loader.load();

            DashboardController controller = loader.getController();
            controller.populateTable(userIdForManager);

            Scene newScene = new Scene(root);
            primaryStage.setScene(newScene);
        } catch (IOException e) {
            System.out.println("failed to load: "+ "/project/fileshare/dashboard.fxml");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error populating dashboard:");
            throw new RuntimeException(e);
        }
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
