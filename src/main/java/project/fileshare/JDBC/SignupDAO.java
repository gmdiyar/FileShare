package project.fileshare.JDBC;

import java.sql.*;

public class SignupDAO {

    public static void trySignUp(String username, String email, String password_hash, int iterations, byte[] salt) throws SQLException {
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/filesharemain",
                "root",
                "369369"
        );

        connection.setAutoCommit(false);

        PreparedStatement insertUser = connection.prepareStatement("INSERT INTO users(username, email) VALUES(?, ?)");
        PreparedStatement insertHash = connection.prepareStatement("INSERT INTO password_hash(username, salt, iterations, password_hash) VALUES(?, ?, ?, ?)");

        try {
            insertUser.setString(1, username);
            insertUser.setString(2, email);
            insertUser.executeUpdate();

            insertHash.setString(1, username);
            insertHash.setBytes(2, salt);
            insertHash.setInt(3, iterations);
            insertHash.setString(4, password_hash);
            insertHash.executeUpdate();

            connection.commit();
            System.out.println("Registered");

        } catch (SQLException e) {
            System.out.println("cant insert info:" + e.getMessage());
            throw new RuntimeException(e);
        }
        finally {
            connection.close();
        }
    }
}
