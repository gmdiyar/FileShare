package project.fileshare.Controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import static project.fileshare.Tools.PasswordHasher.*;
import static project.fileshare.Tools.Rules.ensureUsername;
import static project.fileshare.JDBC.SignupDAO.trySignUp;
public class SignUpController {


    public TextField usernameField;
    public TextField emailField;
    public PasswordField passwordOne;
    public PasswordField passwordTwo;

    public void signUp(ActionEvent actionEvent) throws Exception {
        if (ensureUsername(usernameField.getText()) && passwordOne.getText().equals(passwordTwo.getText())){
            int iterations = getIterations();
            byte[] salt = generateSalt();
            trySignUp(usernameField.getText(), emailField.getText(), HashPassword(passwordOne.getText(), iterations, salt), iterations, salt);
        } else{
            System.out.println("cant sign up");
        }
    }
}
