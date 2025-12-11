package project.fileshare.JDBC;

import java.sql.*;
import java.util.regex.Pattern;

import static project.fileshare.JDBC.LoginDAO.*;

public class SignupDAO {

    private static final String DB_URL = URL;
    private static final String DB_USER = USER;
    private static final String DB_PASSWORD = SQL_PASSWORD;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
            "^[A-Za-z0-9_-]{3,20}$"
    );

    public static void trySignUp(String username, String email, String password_hash, int iterations, byte[] salt) throws SQLException {
        validateUsername(username);
        validateEmail(email);

        if (usernameExists(username)) {
            throw new SQLException("Username already exists");
        }
        if (emailExists(email)) {
            throw new SQLException("Email already exists");
        }

        Connection connection = null;
        PreparedStatement insertUser = null;
        PreparedStatement insertHash = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            connection.setAutoCommit(false);

            insertUser = connection.prepareStatement("INSERT INTO users(username, email) VALUES(?, ?)");
            insertHash = connection.prepareStatement("INSERT INTO password_hash(username, salt, iterations, password_hash) VALUES(?, ?, ?, ?)");

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
            if (connection != null) {
                try {
                    connection.rollback();
                    System.out.println("Transaction rolled back");
                } catch (SQLException rollbackEx) {
                    System.out.println("Error during rollback: " + rollbackEx.getMessage());
                }
            }
            System.out.println("cant insert info:" + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (insertUser != null) insertUser.close();
            if (insertHash != null) insertHash.close();
            if (connection != null) connection.close();
        }
    }

    private static void validateUsername(String username) throws SQLException {
        if (username == null || username.trim().isEmpty()) {
            throw new SQLException("Username cannot be empty");
        }
        if (username.length() < 3 || username.length() > 20) {
            throw new SQLException("Username must be between 3 and 20 characters");
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new SQLException("Username can only contain letters, numbers, underscores, and hyphens");
        }
    }

    private static void validateEmail(String email) throws SQLException {
        if (email == null || email.trim().isEmpty()) {
            throw new SQLException("Email cannot be empty");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new SQLException("Invalid email format");
        }
        if (email.length() > 255) {
            throw new SQLException("Email is too long");
        }
    }

    private static boolean usernameExists(String username) throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE username = ?");
            ps.setString(1, username);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (connection != null) connection.close();
        }
    }

    private static boolean emailExists(String email) throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE email = ?");
            ps.setString(1, email);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (connection != null) connection.close();
        }
    }
}