package project.fileshare.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static project.fileshare.Tools.PasswordHasher.*;
import static project.fileshare.Tools.Rules.ensurePassword;
import static project.fileshare.Tools.Rules.ensureUsername;
import static project.fileshare.JDBC.SignupDAO.trySignUp;
public class SignUpController {


    public TextField usernameField;
    public TextField emailField;
    public PasswordField passwordOne;
    public PasswordField passwordTwo;

    public void signUp(ActionEvent actionEvent) throws Exception {
        if (ensureUsername(usernameField.getText()) && ensurePassword(passwordOne.getText()) && passwordOne.getText().equals(passwordTwo.getText())){
            int iterations = getIterations();
            byte[] salt = generateSalt();
            trySignUp(usernameField.getText(), emailField.getText(), HashPassword(passwordOne.getText(), iterations, salt), iterations, salt);
        } else{
            System.out.println("cant sign up");
        }
    }

    public void backToLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/project/fileshare/login-page.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
