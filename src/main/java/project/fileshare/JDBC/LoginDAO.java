package project.fileshare.JDBC;

import java.sql.*;
import static project.fileshare.Tools.PasswordHasher.HashPassword;
import project.fileshare.Controllers.LoginController.*;
import project.fileshare.Controllers.SceneManager;

public class LoginDAO {

    public static void validateLogin(String username, String password) throws Exception {
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/filesharemain",
                "root",
                "369369"
        );

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT username FROM USERS");

        while(resultSet.next()){
            if (username.equals(resultSet.getString("username"))){
                validatePassword(HashPassword(password, getIterationsFromDB(username), getSaltByUsername(username)), username);
            }
        }
    }

    public static int getIterationsFromDB(String username) throws SQLException {
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/filesharemain",
                "root",
                "369369"
        );

        PreparedStatement getIterationsByUsername = connection.prepareStatement("SELECT iterations FROM password_hash WHERE username = ?");
        getIterationsByUsername.setString(1, username);
        ResultSet rs = getIterationsByUsername.executeQuery();

        if (rs.next()){
            return rs.getInt("iterations");
        } else {
            throw new SQLException("User not found: " + username);
        }
    }

    public static byte[] getSaltByUsername(String username) throws SQLException {
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/filesharemain",
                "root",
                "369369"
        );

        PreparedStatement getSaltByUsername = connection.prepareStatement("SELECT salt FROM password_hash WHERE username = ?");
        getSaltByUsername.setString(1, username);
        ResultSet rs = getSaltByUsername.executeQuery();

        if (rs.next()){
            return rs.getBytes("salt");
        } else {
            throw new SQLException("User not found: " + username);
        }
    }

    public static void validatePassword(String passwordHash, String username) throws SQLException {
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/filesharemain",
                "root",
                "369369"
        );

        PreparedStatement getPasswordHash = connection.prepareStatement("SELECT password_hash FROM password_hash WHERE username = ?");
        getPasswordHash.setString(1, username);
        ResultSet rs = getPasswordHash.executeQuery();

        while(rs.next()){
            if (passwordHash.equals(rs.getString("password_hash"))) {
                SceneManager.getInstance().switchToDashboard();
            }
        }
    }


}