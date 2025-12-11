package project.fileshare.JDBC;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;
import static project.fileshare.Tools.PasswordHasher.HashPassword;
import project.fileshare.Controllers.SceneManager;

public class LoginDAO {

    public static final String URL;
    public static final String USER;
    public static final String SQL_PASSWORD;

    static {
        Properties props = new Properties();
        try (InputStream input = LoginDAO.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new IOException("Unable to find db.properties in resources");
            }
            props.load(input);
            URL = props.getProperty("db.url");
            USER = props.getProperty("db.user");
            SQL_PASSWORD = props.getProperty("db.password");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database properties", e);
        }
    }

    public static void validateLogin(String username, String password) throws Exception {
        Connection connection = DriverManager.getConnection(
                URL,
                USER,
                SQL_PASSWORD
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
                URL,
                USER,
                SQL_PASSWORD
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
                URL,
                USER,
                SQL_PASSWORD
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
                URL,
                USER,
                SQL_PASSWORD
        );

        PreparedStatement getPasswordHash = connection.prepareStatement("SELECT password_hash FROM password_hash WHERE username = ?");
        PreparedStatement getUserId = connection.prepareStatement("SELECT user_id FROM users WHERE username = ?");

        getPasswordHash.setString(1, username);
        getUserId.setString(1, username);


        ResultSet hashes = getPasswordHash.executeQuery();
        ResultSet userId = getUserId.executeQuery();

        while(hashes.next() && userId.next()){
            if (passwordHash.equals(hashes.getString("password_hash"))) {
                SceneManager.userIdForManager = userId.getInt("user_id");
                SceneManager.getInstance().switchToDashboard();
            }
        }
    }


}