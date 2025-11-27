package project.fileshare.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import project.fileshare.JDBC.Connect;
import project.fileshare.JDBC.LoginDAO;

import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;
    @FXML
    public void loginButton(ActionEvent event) throws SQLException {

        String username = usernameField.getText();
        String password = passwordField.getText();

        LoginDAO.Validate(username, password);
    }

    public void signUp(ActionEvent actionEvent) {


    }
}
