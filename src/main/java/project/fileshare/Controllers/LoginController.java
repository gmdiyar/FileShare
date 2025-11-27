package project.fileshare.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import project.fileshare.JDBC.LoginDAO;

import static project.fileshare.JDBC.LoginDAO.validateLogin;

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

    public void signUp(ActionEvent actionEvent) {


    }
}
