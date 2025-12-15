package project.fileshare.Controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class SceneManager {

    // This gets set when the user is successfully logged in. Can be referenced
    // from other classes since it's static, needed for many DAO methods to work since it's how
    // the DAOs know which user is signed in.

    public static int userIdForManager = 0;

    private static SceneManager instance;
    private Stage primaryStage;

    private SceneManager() {}

    // Singleton pattern implementation - ensures only one SceneManager instance exists.
    // Returns the existing instance or creates one if it doesn't exist yet.

    public static SceneManager getInstance(){
        if (instance == null){
            instance = new SceneManager();
        }
        return instance;
    }

    // Sets the primary stage that will be used for all scene switches.

    public void setPrimaryStage(Stage stage){
        this.primaryStage = stage;
    }

    // Switches to the signup page.

    public void switchToSignup() {
        switchToScene("/project/fileshare/sign-up.fxml");
    }

    // Switches to the login page.

    public void switchToLoginPage() {
        switchToScene("/project/fileshare/login-page.fxml");
    }

    // Switches to the dashboard and populates the file tables for the logged-in user.
    // If the user has no files on record, it handles the SQLException gracefully.

    public void switchToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/project/fileshare/dashboard.fxml"));
            Parent root = loader.load();
            DashboardController controller = loader.getController();

            try {
                controller.populateTable(userIdForManager);
            } catch (SQLException e) {
                System.out.println("No files on record for this user.");
            }

            Scene newScene = new Scene(root);
            primaryStage.setScene(newScene);
        } catch (IOException e) {
            System.out.println("Failed to load: "+ "/project/fileshare/dashboard.fxml");
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Generic method for switching to any scene given an FXML file path.

    public void switchToScene(String fxmlFile){
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Scene newScene = new Scene(root);
            primaryStage.setScene(newScene);
        } catch (IOException e) {
            System.out.println("Failed to load: "+ fxmlFile);
            e.printStackTrace();
        }
    }
}