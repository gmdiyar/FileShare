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

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static project.fileshare.Controllers.SceneManager.userIdForManager;
import static project.fileshare.JDBC.FilesDAO.*;

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
    public void initialize() {

        ContextMenu contextMenu = new ContextMenu();

        MenuItem deleteItem = new MenuItem("Delete");
        MenuItem shareItem = new MenuItem("Share with...");
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

        detailsItem.setOnAction(event -> {
            FileEntry selectedFile = filesTable.getSelectionModel().getSelectedItem();
            if (selectedFile != null) {
                handleDetails(selectedFile);
            }
        });

        contextMenu.getItems().addAll(deleteItem, shareItem, detailsItem);

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
        uploadFileInfo(file.getName(),
                file.getAbsolutePath(),
                getFileExtension(file),
                file.length(),
                userIdForManager);
        } populateTable(userIdForManager);
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
        // TODO: Show file details
        System.out.println("Details: " + file.getFileName());
    }

    public void logOut(ActionEvent actionEvent) {
        SceneManager.getInstance().switchToLoginPage();
    }
}