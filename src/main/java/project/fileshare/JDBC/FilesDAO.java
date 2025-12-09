package project.fileshare.JDBC;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

        try {
            while (rs.next()){
                fileNames.add(rs.getString("file_name"));
            }
        } catch (SQLException e) {
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

        try {
            while (rs.next()){
                fileTypes.add(rs.getString("type"));
            }
        } catch (SQLException e) {
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

        try {
            while (rs.next()){
                fileSizes.add(rs.getString("size"));
            }
        } catch (SQLException e) {
            throw new SQLException("User not found: " + userId);
        }
        return fileSizes;
    }

    public static void uploadFileInfo(String name, String path, String type, long size, int owner_id) throws SQLException {
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/filesharemain",
                "root",
                "369369"
        );

        PreparedStatement ps = connection.prepareStatement("insert into files(file_name, file_path, type, size, owner) values (?, ?, ?, ?, ?)");
        ps.setString(1, name);
        ps.setString(2, path);
        ps.setString(3, type);
        ps.setLong(4, size);
        ps.setInt(5, owner_id);

        try {
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Cant upload file info into SQL database:" + e);
            throw new RuntimeException(e);
        } finally {
            connection.close();
        }
    }

    public static void uploadFileInfoWithBlob(String name, String path, String type, long size, int owner_id, File file) throws SQLException, IOException {
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/filesharemain",
                "root",
                "369369"
        );

        PreparedStatement ps = connection.prepareStatement(
                "insert into files(file_name, file_path, type, size, owner, file_data) values (?, ?, ?, ?, ?, ?)"
        );

        ps.setString(1, name);
        ps.setString(2, path);
        ps.setString(3, type);
        ps.setString(4, formatFileSize(size));
        ps.setInt(5, owner_id);

        try (FileInputStream fis = new FileInputStream(file)) {
            ps.setBinaryStream(6, fis, (int) file.length());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Can't upload file info into SQL database: " + e);
            throw new RuntimeException(e);
        } finally {
            connection.close();
        }
    }

    private static String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        int z = (63 - Long.numberOfLeadingZeros(size)) / 10;
        return String.format("%.1f %sB", (double) size / (1L << (z * 10)), " KMGTPE".charAt(z));
    }

}