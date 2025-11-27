package project.fileshare.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import project.fileshare.JDBC.LoginDAO;
import java.io.IOException;
import java.util.Objects;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;

    @FXML
    public void loginButton(ActionEvent event) throws Exception {

        String username = usernameField.getText();
        String password = passwordField.getText();

        LoginDAO.validateLogin(username, password);
    }

    @FXML
    void switchToSignUp(ActionEvent event) throws IOException {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/project/fileshare/sign-up.fxml")));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("failed to initialize new scene.");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
