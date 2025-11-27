package project.fileshare.JDBC;

import java.sql.*;

public class SignupDAO {

    public static void trySignUp(String username, String email, String password_hash) throws SQLException {
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/filesharemain",
                "root",
                "369369"
        );

        PreparedStatement insertUser = connection.prepareStatement("INSERT INTO users(username, email, password_hash) VALUES(?, ?, ?)");
        try {
            insertUser.setString(1, username);
            insertUser.setString(2, email);
            insertUser.setString(3, password_hash);

            insertUser.executeUpdate();
            System.out.println("Registered");
        } catch (SQLException e) {
            System.out.println("cant insert info");
            throw new RuntimeException(e);
        }
    }
}
