package project.fileshare.JDBC;

import java.sql.*;

import static project.fileshare.Tools.PasswordValidator.HashPassword;

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
                validatePassword(HashPassword(password, getIterationsFromDB(), getSaltFromDB()));
            }
        }
    }

    public static int getIterationsFromDB() throws SQLException {
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/filesharemain",
                "root",
                "369369"
        );

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT iterations FROM password_hash");

        int iterations = 0;
        while(resultSet.next()){
                iterations = resultSet.getInt("iterations");
        }
        return iterations;
    }

    public static byte[] getSaltFromDB() throws SQLException {
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/filesharemain",
                "root",
                "369369"
        );

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT salt FROM password_hash");

        byte[] salt = new byte[0];
        while(resultSet.next()){
            salt = resultSet.getBytes("salt");
        }
        return salt;
    }

    public static void validatePassword(String passwordHash) throws SQLException {
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/filesharemain",
                "root",
                "369369"
        );

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT username FROM USERS");

        while(resultSet.next()){
            if (passwordHash.equals(resultSet.getString("password_hash"))) {
            }
        }
    }
}