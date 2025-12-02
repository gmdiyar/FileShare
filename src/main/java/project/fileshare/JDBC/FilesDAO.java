package project.fileshare.JDBC;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FilesDAO {

    public static List<String> getFileName(int userId) throws SQLException {
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/filesharemain",
                "root",
                "369369"
        );

        List<String> fileNames = new ArrayList<String>();

        PreparedStatement getFileNames = connection.prepareStatement("SELECT file_name FROM files WHERE owner = ?");
        getFileNames.setInt(1, userId);
        ResultSet rs = getFileNames.executeQuery();

        if (rs.next()){
            fileNames.add(rs.getString("file_name"));
        } else {
            throw new SQLException("User not found: " + userId);
        }
        return fileNames;
    }

    public static List<String> getFileType(int userId) throws SQLException {
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/filesharemain",
                "root",
                "369369"
        );

        List<String> fileTypes = new ArrayList<String>();

        PreparedStatement getFileTypes = connection.prepareStatement("SELECT type FROM files WHERE owner = ?");
        getFileTypes.setInt(1, userId);
        ResultSet rs = getFileTypes.executeQuery();

        if (rs.next()){
            fileTypes.add(rs.getString("type"));
        } else {
            throw new SQLException("User not found: " + userId);
        }
        return fileTypes;
    }

    public static List<String> getFileSize(int userId) throws SQLException {
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/filesharemain",
                "root",
                "369369"
        );

        List<String> fileSizes = new ArrayList<String>();

        PreparedStatement getFileSizes = connection.prepareStatement("SELECT size FROM files WHERE owner = ?");
        getFileSizes.setInt(1, userId);
        ResultSet rs = getFileSizes.executeQuery();

        if (rs.next()){
            fileSizes.add(rs.getString("size"));
        } else {
            throw new SQLException("User not found: " + userId);
        }
        return fileSizes;
    }
}
