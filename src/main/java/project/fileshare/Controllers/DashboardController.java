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

import static project.fileshare.Controllers.SceneManager.userIdForManager;
import static project.fileshare.JDBC.FilesDAO.*;
import static project.fileshare.JDBC.FilesShareDAO.*;

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

        ContextMenu contextMenu = new ContextMenu();

        MenuItem deleteItem = new MenuItem("Delete");
        MenuItem shareItem = new MenuItem("Share with...");
        MenuItem downloadItem = new MenuItem("Download");
        MenuItem detailsItem = new MenuItem("Details");

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

        contextMenu.getItems().addAll(deleteItem, shareItem, downloadItem, detailsItem);

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

    public void populateTable(int id) throws SQLException {

        List<String> fileNames = getFileName(id);
        List<String> fileTypes = getFileType(id);
        List<String> fileSizes = getFileSize(id);

        ObservableList<FileEntry> files = FXCollections.observableArrayList();

        for(int i = 0; i < fileNames.size() && i < fileTypes.size() && i < fileSizes.size(); i++){
            files.add(new FileEntry(fileNames.get(i), fileTypes.get(i), fileSizes.get(i)));
        }

        fileTableName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFileName()));
        fileTableType.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFileType()));
        fileTableSize.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFileSize()));

        filesTable.setItems(files);

        populateSharedFilesTable(id);

    }

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

    private String getFileExtension(File file) {
        String fileName = file.getName();
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return fileName.substring(lastIndexOf + 1);
    }

    public void uploadFile(ActionEvent actionEvent) throws SQLException {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
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
        populateTable(userIdForManager);
    }

    private void handleDelete(FileEntry file) throws SQLException {
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/filesharemain",
                "root",
                "369369"
        );

        PreparedStatement ps = connection.prepareStatement("delete from files where file_name = ?");
        ps.setString(1, file.getFileName());
        ps.executeUpdate();
        populateTable(userIdForManager);
        System.out.println("Deleted: " + file.getFileName());

        connection.close();
    }

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

    private void handleDetails(FileEntry file) {
        // TODO: Show details
        System.out.println("Details: " + file.getFileName());
    }

    private void handleDownload(FileEntry file, boolean isShared) {
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Download Location");
            File directory = directoryChooser.showDialog(null);

            if (directory != null) {
                byte[] fileData;

                if (isShared) {
                    fileData = getSharedFileData(file.getFileName(), userIdForManager);
                } else {
                    fileData = getFileData(file.getFileName(), userIdForManager);
                }

                if (fileData != null && fileData.length > 0) {
                    File downloadFile = new File(directory, file.getFileName());

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

    public void logOut(ActionEvent actionEvent) {
        SceneManager.getInstance().switchToLoginPage();
    }
}