package project.fileshare.Controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.SQLException;
import java.util.List;

import static project.fileshare.JDBC.FilesDAO.*;
import static project.fileshare.JDBC.FilesDAO.getFileType;

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

    public void shareMenu(ActionEvent actionEvent) {
        //todo
    }

    public void logOut(ActionEvent actionEvent) {
        SceneManager.getInstance().switchToLoginPage();
    }
}
