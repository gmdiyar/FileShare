package project.fileshare.Controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.DirectoryChooser;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.mysql.cj.conf.PropertyKey.PASSWORD;
import static project.fileshare.Controllers.SceneManager.userIdForManager;
import static project.fileshare.JDBC.FilesDAO.*;
import static project.fileshare.JDBC.FilesShareDAO.*;
import static project.fileshare.JDBC.LoginDAO.*;

public class DashboardController {

    public static class FileEntry{

        private final String fileName;
        private final String fileType;
        private final String fileSize;

        public FileEntry(String fileName, String fileType, String fileSize){
            this.fileName = fileName;
            this.fileType = fileType;
            this.fileSize = fileSize;
        }
        public String getFileName() {return fileName;}
        public String getFileType() {return fileType;}
        public String getFileSize() {return fileSize;}
    }

    @FXML
    public TableView<FileEntry> filesTable;
    @FXML
    public TableColumn<FileEntry, String> fileTableName;
    @FXML
    public TableColumn<FileEntry, String> fileTableType;
    @FXML
    public TableColumn<FileEntry, String> fileTableSize;

    @FXML
    public TableView<FileEntry> sharedFilesTable;
    @FXML
    public TableColumn<FileEntry, String> sharedFileTableName;
    @FXML
    public TableColumn<FileEntry, String> sharedFileTableType;
    @FXML
    public TableColumn<FileEntry, String> sharedFileTableSize;

    @FXML
    public void initialize() {

        // The initialize method begins with defining the context menu, as well as
        // the buttons that it contains.
        
        ContextMenu contextMenu = new ContextMenu();

        MenuItem deleteItem = new MenuItem("Delete");
        MenuItem shareItem = new MenuItem("Share with...");
        MenuItem downloadItem = new MenuItem("Download");
        MenuItem detailsItem = new MenuItem("Details");

        /// The following methods: deleteItem, shareItem, downloadItem, and detailsItem all do the same thing:
        /// 
        /// 1. Each method is a listener that defines the method to be executed when a button is pressed in the
        ///    context menu.
        /// 
        /// 2. All methods ensure that the selected file is not null before proceeding to call the appropriate
        ///    DAO methods.
        /// 
        /// 3. If the file is not null, the appropriate method is then called with the file as the argument.

        deleteItem.setOnAction(event -> {
            FileEntry selectedFile = filesTable.getSelectionModel().getSelectedItem();
            if (selectedFile != null) {
                try {
                    handleDelete(selectedFile);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        
        shareItem.setOnAction(event -> {
            FileEntry selectedFile = filesTable.getSelectionModel().getSelectedItem();
            if (selectedFile != null) {
                handleShare(selectedFile);
            }
        });

        downloadItem.setOnAction(event -> {
            FileEntry selectedFile = filesTable.getSelectionModel().getSelectedItem();
            if (selectedFile != null) {
                try {
                    handleDownload(selectedFile, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        detailsItem.setOnAction(event -> {
            FileEntry selectedFile = filesTable.getSelectionModel().getSelectedItem();
            if (selectedFile != null) {
                handleDetails(selectedFile);
            }
        });

        // Adds all the defined buttons to the context menu.
        contextMenu.getItems().addAll(deleteItem, shareItem, downloadItem, detailsItem);

        // This part binds the context menu to the table view rows.
        // This means that the context menu can only be called on values of the table view columns
        // (which is necessary because the columns contain a file object to be called on).

        filesTable.setRowFactory(tv -> {
            TableRow<FileEntry> row = new TableRow<>();
            row.setContextMenu(contextMenu);

            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );

            return row;
        });

        setupSharedFilesContextMenu();
    }

    // The setupSharedFilesContextMenu does the exact same thing as the initialize method
    // except that it only defines two buttons: download and details.

    private void setupSharedFilesContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem downloadItem = new MenuItem("Download");
        MenuItem detailsItem = new MenuItem("Details");

        downloadItem.setOnAction(event -> {
            FileEntry selectedFile = sharedFilesTable.getSelectionModel().getSelectedItem();
            if (selectedFile != null) {
                try {
                    handleDownload(selectedFile, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        detailsItem.setOnAction(event -> {
            FileEntry selectedFile = sharedFilesTable.getSelectionModel().getSelectedItem();
            if (selectedFile != null) {
                handleDetails(selectedFile);
            }
        });

        contextMenu.getItems().addAll(downloadItem, detailsItem);

        sharedFilesTable.setRowFactory(tv -> {
            TableRow<FileEntry> row = new TableRow<>();
            row.setContextMenu(contextMenu);

            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );

            return row;
        });
    }

    /// This is the method that populates the 'My files' table. 
    /// It is called when the user is sent to the dashboard after sign in.
    
    public void populateTable(int id) throws SQLException {

        // Calls three methods from filesDAO that query the database for the
        // needed file info.

        List<String> fileNames = getFileName(id);
        List<String> fileTypes = getFileType(id);
        List<String> fileSizes = getFileSize(id);

        // Initializes an observable array list (what the JavaFX tableview can iterate through).

        ObservableList<FileEntry> files = FXCollections.observableArrayList();

        // Iterates for all file names, types, and sizes and adds the file as a fileEntry object to the 
        // previously defined observable list 'files'.

        for(int i = 0; i < fileNames.size() && i < fileTypes.size() && i < fileSizes.size(); i++){
            files.add(new FileEntry(fileNames.get(i), fileTypes.get(i), fileSizes.get(i)));
        }

        /// This is the section that actually sets the cell values in the table. It does this by:
        /// 
        /// 1. Calling the setCellValueFactory method on the fileTableName tableColumn object.
        ///    This takes a lambda function that essentially tells the tableview how to get the 
        ///    values for the given cell.
        /// 
        /// 2. Defining the lambda function "given cellData, call the file(name, type, size) getter for the value of 
        ///    that cellData object then set that value to a simpleStringProperty" (so that it can be read).

        fileTableName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFileName()));
        fileTableType.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFileType()));
        fileTableSize.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFileSize()));

        filesTable.setItems(files);

        // Populates the 'Shared with me' table for the logged in user (using the ID from the scene manager).

        populateSharedFilesTable(id);

    }

    // This method does the exact same thing as the populateTable method except it only calls the DAO methods that 
    // access the appropriate table. 

    public void populateSharedFilesTable(int userId) throws SQLException {
        List<String> sharedFileNames = getSharedFileNames(userId);
        List<String> sharedFileTypes = getSharedFileTypes(userId);
        List<String> sharedFileSizes = getSharedFileSizes(userId);

        ObservableList<FileEntry> sharedFiles = FXCollections.observableArrayList();

        for(int i = 0; i < sharedFileNames.size() && i < sharedFileTypes.size() && i < sharedFileSizes.size(); i++){
            sharedFiles.add(new FileEntry(sharedFileNames.get(i), sharedFileTypes.get(i), sharedFileSizes.get(i)));
        }

        sharedFileTableName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFileName()));
        sharedFileTableType.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFileType()));
        sharedFileTableSize.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFileSize()));

        sharedFilesTable.setItems(sharedFiles);
    }

    /// Gets the file extension of a given file by
    /// 
    /// 1. Getting the file name as a string (contains extension)
    /// 
    /// 2. Gets the index of where the . appears as an int (anything after it is the extension).
    /// 
    /// 3. If the file has no extension (the . is not found), returns an empty string.
    /// 
    /// 4. If the file does have an extension (. is found), returns everything after the . as a string. 

    private String getFileExtension(File file) {
        String fileName = file.getName();
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return fileName.substring(lastIndexOf + 1);
    }

    /// This is the method that is attached to the 'upload file' button on the dashboard.

    public void uploadFile(ActionEvent actionEvent) throws SQLException {

        // It first creates a new fileChooser object, then sets the 
        // variable 'file' of type File to the showOpenDialog method 
        // from the fileChooser object.

        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);

        // If the file isn't null, it tries to upload the file with all the data
        // (file name, type, extension, length, and data as a BLOB) through the uploadFileInfoWithBlob method
        // in the filesDAO class.
        // If there was an error with the IO, it throws an IOException with the exception message.

        if (file != null){
            try {
                uploadFileInfoWithBlob(file.getName(),
                        file.getAbsolutePath(),
                        getFileExtension(file),
                        file.length(),
                        userIdForManager,
                        file);
            } catch (IOException e) {
                System.out.println("Error uploading file: " + e.getMessage());
                e.printStackTrace();
            }
        }
        // Repopulates the table after a new file was uploaded so that the user can see up-to-date data.
        populateTable(userIdForManager);
    }
    // Simple method that deletes the file entry from the files table in the SQL database and repopulates 
    // the table.

    private void handleDelete(FileEntry file) throws SQLException {
        Connection connection = DriverManager.getConnection(
                URL,
                USER,
                SQL_PASSWORD
        );

        PreparedStatement ps = connection.prepareStatement("delete from files where file_name = ?");
        ps.setString(1, file.getFileName());
        ps.executeUpdate();
        populateTable(userIdForManager);
        System.out.println("Deleted: " + file.getFileName());

        connection.close();
    }

    /// This method is crucial for the share functionality of the app.
    /// 
    /// 1. Starts off by taking a FileEntry object (file) 
    ///
    /// 2. Tries to switch scenes to the share-dialog view file.
    /// 
    /// 3. Sets the file field in the share-dialog controller to the file given to it by argument.
    /// 
    /// 4. Among other things, it sets the modality of the window to APPLICATION_MODAL so that the 
    ///    view is a pop-up window that will not allow interaction with the dashboard view unless 
    ///    it is closed.
    /// 
    /// 5. Once the file sharing DAO is handled, it repopulates the table.

    private void handleShare(FileEntry file) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/project/fileshare/share-dialog.fxml"));
            Parent root = loader.load();
            ShareMenuController controller = loader.getController();
            controller.setFile(file);

            Stage dialogStage = new Stage();
            controller.setDialogStage(dialogStage);

            dialogStage.setTitle("Share file");
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.showAndWait();

            populateTable(userIdForManager);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // Should show details or preview of the contents of the file.
    private void handleDetails(FileEntry file) {
        // TODO: Show details
        System.out.println("Details: " + file.getFileName());
    }

    // When a user presses the download button, this method is called.
    // It is mostly comprised of try and if statements, the DAO logic is
    // referenced from the fileShareDAO.

    private void handleDownload(FileEntry file, boolean isShared) {

        // Tries to launch a directory chooser (which differs from a file chooser in that it
        // doesn't allow the user to select a file, and returns a path/directory as opposed 
        // to a file with file info).

        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Download Location");
            File directory = directoryChooser.showDialog(null);

            // If the directory isn't null, initialize a byte array called fileData.

            if (directory != null) {
                byte[] fileData;

                // If the file is 'shared' meaning that it was shared with the current user
                // from another user, download it using the getSharedFileData method from 
                // fileShares DAO.

                if (isShared) {
                    fileData = getSharedFileData(file.getFileName(), userIdForManager);
                } else {

                    // If the file isn't shared (meaning it belongs to the current user) 
                    // then get the file data using the getFileData method.

                    fileData = getFileData(file.getFileName(), userIdForManager);
                }

                // If the file has any data (able to be read), initialize a new variable named
                // downloadFile of type File, and set it to a new File object.

                if (fileData != null && fileData.length > 0) {
                    File downloadFile = new File(directory, file.getFileName());

                    // This is the part that actually downloads the file. It tries to write the 
                    // file data into the selected directory using a file output stream.

                    try (FileOutputStream fos = new FileOutputStream(downloadFile)) {
                        fos.write(fileData);
                        System.out.println("File downloaded successfully to: " + downloadFile.getAbsolutePath());
                    }
                } else {
                    System.out.println("No file data found for: " + file.getFileName());
                }
            }
        } catch (Exception e) {
            System.out.println("Error downloading file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Switches to login view upon press of the logout button.
    
    public void logOut(ActionEvent actionEvent) {
        SceneManager.getInstance().switchToLoginPage();
    }
}