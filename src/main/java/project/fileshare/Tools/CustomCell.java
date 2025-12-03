package project.fileshare.Tools;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;

public class CustomCell <T> extends ListCell<T> {

    @Override
    public void updateItem(T item, boolean isEmpty){
        super.updateItem(item, isEmpty);
        if (isEmpty || item == null){
            setText(null);
            setGraphic(null);
        } else {
            ComboBox<String> comboBox = new ComboBox<>();
            comboBox.getItems().addAll("Delete", "Share with...", "Details");

            Button browseFileButton = new Button("Add a file");
            browseFileButton.setOnAction( event -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select file");
                File selectedFile = fileChooser.showOpenDialog(getScene().getWindow());

                if (selectedFile != null) {
                    //todo: handle file
                }
            });

            HBox cellLayout = new HBox(10);
            cellLayout.getChildren().addAll(comboBox, browseFileButton);

            setGraphic(cellLayout);
        }
    }
}
