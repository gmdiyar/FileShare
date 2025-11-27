package project.fileshare.JDBC;

import java.sql.*;

public class LoginDAO {

    public static void Validate(String username, String password) throws SQLException {
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/filesharemain",
                "root",
                "369369"
        );

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM USERS");

        while(resultSet.next()){
            if (username.equals(resultSet.getString("username"))){
                System.out.println("Welcome, "+ username );
            }
        }
    }

}
