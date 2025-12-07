package project.fileshare.JDBC;

import java.sql.*;

import static project.fileshare.Controllers.SceneManager.userIdForManager;

public class FilesShareDAO {

    public static int getFileID(String fileName, int ownerID) throws SQLException {
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/filesharemain",
                "root",
                "369369"
        );

        PreparedStatement ps = connection.prepareStatement("select file_id from files where file_name = ? and owner = ?");
        ps.setString(1, fileName);
        ps.setInt(2, ownerID);

        ResultSet rs = ps.executeQuery();
        int fileID = 0;
        while (rs.next()) {
            fileID = rs.getInt("file_id");
        } connection.close();
        return fileID;
    }

    public static int getIdFromUsername(String username) throws SQLException {
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/filesharemain",
                "root",
                "369369"
        );

        PreparedStatement ps = connection.prepareStatement("select user_id from users where username = ?");
        ps.setString(1, username);

        ResultSet rs = ps.executeQuery();
        int userID = 0;
        while (rs.next()) {
            userID = rs.getInt("user_id");
        } connection.close();
        return userID;
    }

    public static void shareFile(int fileID, int sharedWithID, String permission, int ownerID) throws SQLException {

        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/filesharemain",
                "root",
                "369369"
        );

        PreparedStatement ps = connection.prepareStatement("insert into file_shares(file_id, shared_with, permissions, owner_id) values(?, ?, ?, ?)");
        ps.setInt(1, fileID);
        ps.setInt(2, sharedWithID);
        ps.setString(3, permission);
        ps.setInt(4, ownerID);

        ps.executeUpdate();

        connection.close();
    }

}
