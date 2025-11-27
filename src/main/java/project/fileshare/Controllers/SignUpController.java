package project.fileshare.Controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import static project.fileshare.Tools.PasswordHasher.generateHashedPassword;
import static project.fileshare.Tools.Rules.ensureUsername;
import static project.fileshare.JDBC.SignupDAO.trySignUp;
public class SignUpController {


    public TextField usernameField;
    public TextField emailField;
    public PasswordField passwordOne;
    public PasswordField passwordTwo;

    public void signUp(ActionEvent actionEvent) throws Exception {
        if (ensureUsername(usernameField.getText()) && passwordOne.getText().equals(passwordTwo.getText())){
            trySignUp(usernameField.getText(), emailField.getText(), generateHashedPassword(passwordOne.getText()));
        } else{
            System.out.println("cant sign up");
        }
    }
}
