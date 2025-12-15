/// Almost all of these methods are doing the same thing:
/// 
/// 1. Connect to MySQL database using info from a hidden properties file.
/// 
/// 2. Write a prepared SQL statement
/// 
/// 3. Execute the prepared statement
/// 
/// 4. Loop through the results if query, nothing if it was an update.   

package project.fileshare.JDBC;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static project.fileshare.JDBC.LoginDAO.*;

public class FilesDAO {

    /// This method retrieves all file names owned by a specific user from the database.
    /// 
    /// 1. Establishes a connection to the MySQL database using credentials from LoginDAO.
    /// 
    /// 2. Initializes an empty list to store file names.
    /// 
    /// 3. Creates a prepared statement to query file names where the owner matches the given userId.
    /// 
    /// 4. Executes the query and iterates through results, adding each file name to the list.
    /// 
    /// 5. Returns the list of file names, or throws an SQLException if the user is not found.

    public static List<String> getFileName(int userId) throws SQLException {

        // Connecting to SQL server.

        Connection connection = DriverManager.getConnection(
                URL,
                USER,
                SQL_PASSWORD
        );

        // Initialize fileNames list of strings for files to be appended to. 

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

    /// This method does the exact same thing as the getFileName method except it queries for 
    /// file types instead of file names.

    public static List<String> getFileType(int userId) throws SQLException {
        Connection connection = DriverManager.getConnection(
                URL,
                USER,
                SQL_PASSWORD
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

    /// This method does the exact same thing as the getFileName method except it queries for 
    /// file sizes instead of file names.

    public static List<String> getFileSize(int userId) throws SQLException {
        Connection connection = DriverManager.getConnection(
                URL,
                USER,
                SQL_PASSWORD
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

    /// This method inserts file metadata into the database without storing the actual file data.
    /// 
    /// 1. Connects to the MySQL database.
    /// 
    /// 2. Creates a prepared statement to insert file information (name, path, type, size, owner).
    /// 
    /// 3. Sets each parameter in the prepared statement with the provided values.
    /// 
    /// 4. Executes the update to insert the new file record.
    /// 
    /// 5. Closes the connection in the finally block to ensure proper resource cleanup.

    public static void uploadFileInfo(String name, String path, String type, long size, int owner_id) throws SQLException {
        Connection connection = DriverManager.getConnection(
                URL,
                USER,
                SQL_PASSWORD
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

    /// This is the crucial method for uploading files to the database with their actual content.
    /// 
    /// 1. Connects to the MySQL database.
    /// 
    /// 2. Creates a prepared statement to insert file information including the file_data BLOB column.
    /// 
    /// 3. Sets the file metadata parameters (name, path, type, size, owner).
    /// 
    /// 4. Formats the file size using the formatFileSize helper method for human-readable display.
    /// 
    /// 5. Uses a FileInputStream to read the file contents and sets it as a binary stream in the 
    ///    prepared statement.
    /// 
    /// 6. Executes the update to store both file metadata and the actual file data as a BLOB.
    /// 
    /// 7. Closes resources in the finally block to prevent memory leaks.

    public static void uploadFileInfoWithBlob(String name, String path, String type, long size, int owner_id, File file) throws SQLException, IOException {
        Connection connection = DriverManager.getConnection(
                URL,
                USER,
                SQL_PASSWORD
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

    /// This helper method converts raw byte sizes into human-readable format.
    /// 
    /// 1. If the size is less than 1024 bytes, returns the size in bytes (B).
    /// 
    /// 2. Otherwise, calculates the appropriate unit (KB, MB, GB, TB, PB, EB) by determining 
    ///    how many times 1024 can divide the size.
    /// 
    /// 3. Returns a formatted string with one decimal place and the appropriate unit suffix.
    /// 
    /// For example: 1500 bytes becomes "1.5 KB", 2048000 bytes becomes "2.0 MB".

    private static String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        int z = (63 - Long.numberOfLeadingZeros(size)) / 10;
        return String.format("%.1f %sB", (double) size / (1L << (z * 10)), " KMGTPE".charAt(z));
    }

}