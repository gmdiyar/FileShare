package project.fileshare.JDBC;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static project.fileshare.JDBC.LoginDAO.SQL_PASSWORD;
import static project.fileshare.JDBC.LoginDAO.URL;
import static project.fileshare.JDBC.LoginDAO.USER;

public class FilesShareDAO {

    // Gets the file ID from the database by matching the file name and owner ID.
    // Returns 0 if no match is found.

    public static int getFileID(String fileName, int ownerID) throws SQLException {
        Connection connection = DriverManager.getConnection(
                URL,
                USER,
                SQL_PASSWORD
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

    // Gets a user's ID by looking up their username in the users table.
    // Returns 0 if the username doesn't exist.

    public static int getIdFromUsername(String username) throws SQLException {
        Connection connection = DriverManager.getConnection(
                URL,
                USER,
                SQL_PASSWORD
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

    // Inserts a new record into the file_shares table, creating a sharing relationship between
    // the file owner and the user the file is being shared with. Includes permission level
    // (like "read" or "write") for access control.

    public static void shareFile(int fileID, int sharedWithID, String permission, int ownerID) throws SQLException {

        Connection connection = DriverManager.getConnection(
                URL,
                USER,
                SQL_PASSWORD
        );

        PreparedStatement ps = connection.prepareStatement("insert into file_shares(file_id, shared_with, permissions, owner_id) values(?, ?, ?, ?)");
        ps.setInt(1, fileID);
        ps.setInt(2, sharedWithID);
        ps.setString(3, permission);
        ps.setInt(4, ownerID);

        try {
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error sharing file. File might already be shared or username is incorrect: " + e);
        }

        connection.close();
    }

    // Retrieves all file names that have been shared with a specific user.
    // Uses a join between file_shares and files tables to get the actual file names.

    public static List<String> getSharedFileNames(int userId) throws SQLException {
        Connection connection = DriverManager.getConnection(
                URL,
                USER,
                SQL_PASSWORD
        );

        PreparedStatement ps = connection.prepareStatement(
                "select f.file_name from file_shares fs " +
                        "join files f on fs.file_id = f.file_id " +
                        "where fs.shared_with = ?"
        );
        ps.setInt(1, userId);

        ResultSet rs = ps.executeQuery();
        List<String> fileNames = new ArrayList<>();
        while (rs.next()) {
            fileNames.add(rs.getString("file_name"));
        }
        connection.close();
        return fileNames;
    }

    // Does the same thing as getSharedFileNames but retrieves file types instead.

    public static List<String> getSharedFileTypes(int userId) throws SQLException {
        Connection connection = DriverManager.getConnection(
                URL,
                USER,
                SQL_PASSWORD
        );

        PreparedStatement ps = connection.prepareStatement(
                "select f.type from file_shares fs " +
                        "join files f on fs.file_id = f.file_id " +
                        "where fs.shared_with = ?"
        );
        ps.setInt(1, userId);

        ResultSet rs = ps.executeQuery();
        List<String> fileTypes = new ArrayList<>();
        while (rs.next()) {
            fileTypes.add(rs.getString("type"));
        }
        connection.close();
        return fileTypes;
    }

    // Does the same thing as getSharedFileNames but retrieves file sizes instead.

    public static List<String> getSharedFileSizes(int userId) throws SQLException {
        Connection connection = DriverManager.getConnection(
                URL,
                USER,
                SQL_PASSWORD
        );

        PreparedStatement ps = connection.prepareStatement(
                "select f.size from file_shares fs " +
                        "join files f on fs.file_id = f.file_id " +
                        "where fs.shared_with = ?"
        );
        ps.setInt(1, userId);

        ResultSet rs = ps.executeQuery();
        List<String> fileSizes = new ArrayList<>();
        while (rs.next()) {
            String size = rs.getString("size");
            fileSizes.add(size);
        }
        connection.close();
        return fileSizes;
    }

    // Gets the usernames of all the people who have shared files with a specific user.
    // Joins file_shares with users table to get the owner's username.

    public static List<String> getSharedFileOwners(int userId) throws SQLException {
        Connection connection = DriverManager.getConnection(
                URL,
                USER,
                SQL_PASSWORD
        );

        PreparedStatement ps = connection.prepareStatement(
                "select u.username from file_shares fs " +
                        "join users u on fs.owner_id = u.user_id " +
                        "where fs.shared_with = ?"
        );
        ps.setInt(1, userId);

        ResultSet rs = ps.executeQuery();
        List<String> owners = new ArrayList<>();
        while (rs.next()) {
            owners.add(rs.getString("username"));
        }
        connection.close();
        return owners;
    }

    // Gets the permission levels for all files shared with a specific user.
    // Permission levels determine what actions the user can perform (read, write, etc.).

    public static List<String> getSharedFilePermissions(int userId) throws SQLException {
        Connection connection = DriverManager.getConnection(
                URL,
                USER,
                SQL_PASSWORD
        );

        PreparedStatement ps = connection.prepareStatement(
                "select fs.permissions from file_shares fs " +
                        "where fs.shared_with = ?"
        );
        ps.setInt(1, userId);

        ResultSet rs = ps.executeQuery();
        List<String> permissions = new ArrayList<>();
        while (rs.next()) {
            permissions.add(rs.getString("permissions"));
        }
        connection.close();
        return permissions;
    }

    // Helper method that formats raw byte sizes into readable units (B, KB, MB, GB, etc.).

    private static String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        int z = (63 - Long.numberOfLeadingZeros(size)) / 10;
        return String.format("%.1f %sB", (double) size / (1L << (z * 10)), " KMGTPE".charAt(z));
    }

    // Retrieves the actual file data (as a byte array) for a file owned by a specific user.
    // This is what allows files to be downloaded from the database.

    public static byte[] getFileData(String fileName, int userId) throws SQLException {
        Connection connection = DriverManager.getConnection(
                URL,
                USER,
                SQL_PASSWORD
        );

        PreparedStatement ps = connection.prepareStatement(
                "select file_data from files where file_name = ? and owner = ?"
        );
        ps.setString(1, fileName);
        ps.setInt(2, userId);

        ResultSet rs = ps.executeQuery();
        byte[] fileData = null;
        if (rs.next()) {
            fileData = rs.getBytes("file_data");
        }
        connection.close();
        return fileData;
    }

    // Gets file data for a file that was shared with the user (not owned by them).
    // Joins file_shares and files tables to verify the user has access before returning the data.

    public static byte[] getSharedFileData(String fileName, int sharedWithUserId) throws SQLException {
        Connection connection = DriverManager.getConnection(
                URL,
                USER,
                SQL_PASSWORD
        );

        PreparedStatement ps = connection.prepareStatement(
                "select f.file_data from file_shares fs " +
                        "join files f on fs.file_id = f.file_id " +
                        "where f.file_name = ? and fs.shared_with = ?"
        );
        ps.setString(1, fileName);
        ps.setInt(2, sharedWithUserId);

        ResultSet rs = ps.executeQuery();
        byte[] fileData = null;
        if (rs.next()) {
            fileData = rs.getBytes("file_data");
        }
        connection.close();
        return fileData;
    }

    // Gets the file path for a file owned by a specific user.

    public static String getFilePath(String fileName, int userId) throws SQLException {
        Connection connection = DriverManager.getConnection(
                URL,
                USER,
                SQL_PASSWORD
        );

        PreparedStatement ps = connection.prepareStatement(
                "select file_path from files where file_name = ? and owner = ?"
        );
        ps.setString(1, fileName);
        ps.setInt(2, userId);

        ResultSet rs = ps.executeQuery();
        String filePath = null;
        if (rs.next()) {
            filePath = rs.getString("file_path");
        }
        connection.close();
        return filePath;
    }

    // Gets the file path for a file that was shared with the user.
    // Same as getFilePath but for shared files instead of owned files.

    public static String getSharedFilePath(String fileName, int sharedWithUserId) throws SQLException {
        Connection connection = DriverManager.getConnection(
                URL,
                USER,
                SQL_PASSWORD
        );

        PreparedStatement ps = connection.prepareStatement(
                "select f.file_path from file_shares fs " +
                        "join files f on fs.file_id = f.file_id " +
                        "where f.file_name = ? and fs.shared_with = ?"
        );
        ps.setString(1, fileName);
        ps.setInt(2, sharedWithUserId);

        ResultSet rs = ps.executeQuery();
        String filePath = null;
        if (rs.next()) {
            filePath = rs.getString("file_path");
        }
        connection.close();
        return filePath;
    }
}