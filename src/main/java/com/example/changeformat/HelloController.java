package com.example.changeformat;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;

import java.io.File;

public class HelloController {

    @FXML
    private TextField dragAndDropField;

    @FXML
    private ChoiceBox<String> mediaTypeChoiceBox;

    @FXML
    private ChoiceBox<String> inputFormatChoiceBox;

    @FXML
    private ChoiceBox<String> outputFormatChoiceBox;

    @FXML
    private ListView<String> progressListView;

    @FXML
    private ChoiceBox<String> resolutionChoiceBox;

    @FXML
    private ChoiceBox<String> qualityChoiceBox;

    @FXML
    private TextField newWidthField;

    @FXML
    private TextField newHeightField;

    @FXML
    private HBox videoOptions;

    @FXML
    private HBox imageOptions;

    @FXML
    public void initialize() {
        // Medya türü seçeneklerini ayarla
        mediaTypeChoiceBox.setItems(FXCollections.observableArrayList("Video", "Görsel"));
        mediaTypeChoiceBox.setValue("Video");

        // Medya türü değiştiğinde format ve seçenekleri güncelle
        mediaTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                updateFormatChoices(newValue));

        // Varsayılan olarak video formatları ile başlat
        updateFormatChoices("Video");

        // Sürükle ve bırak özelliklerini ayarla
        setupDragAndDrop();
    }

    private void updateFormatChoices(String mediaType) {
        if ("Video".equals(mediaType)) {
            inputFormatChoiceBox.setItems(FXCollections.observableArrayList("MP4", "AVI", "MKV", "MOV"));
            outputFormatChoiceBox.setItems(FXCollections.observableArrayList("MP4", "AVI", "MKV", "MOV", "WMV", "FLV"));
            videoOptions.setVisible(true);
            imageOptions.setVisible(false);
            setVideoOptions();
        } else if ("Görsel".equals(mediaType)) {
            inputFormatChoiceBox.setItems(FXCollections.observableArrayList("JPEG", "PNG", "BMP", "TIFF"));
            outputFormatChoiceBox.setItems(FXCollections.observableArrayList("JPEG", "PNG", "BMP", "TIFF", "GIF", "WEBP"));
            videoOptions.setVisible(false);
            imageOptions.setVisible(true);
        }
    }

    private void setVideoOptions() {
        resolutionChoiceBox.setItems(FXCollections.observableArrayList("1920x1080", "1280x720", "640x360"));
        resolutionChoiceBox.setValue("1920x1080");
        qualityChoiceBox.setItems(FXCollections.observableArrayList("Yüksek", "Orta", "Düşük"));
        qualityChoiceBox.setValue("Yüksek");
    }

    private void setupDragAndDrop() {
        dragAndDropField.setOnDragOver(event -> {
            if (event.getGestureSource() != dragAndDropField && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(javafx.scene.input.TransferMode.COPY);
            }
            event.consume();
        });

        dragAndDropField.setOnDragDropped(event -> {
            var db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                for (File file : db.getFiles()) {
                    dragAndDropField.setText(file.getAbsolutePath());
                    // Burada dosyayı işleme alabilirsiniz
                }
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    @FXML
    private void handleFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Dönüştürülecek Dosyayı Seçin");

        if ("Video".equals(mediaTypeChoiceBox.getValue())) {
            fileChooser.getExtensionFilters().add(new FileChooser.
                    ExtensionFilter("Video Dosyaları", "*.mp4", "*.avi", "*.mkv", "*.mov"));
        } else if ("Görsel".equals(mediaTypeChoiceBox.getValue())) {
            fileChooser.getExtensionFilters().add(new FileChooser.
                    ExtensionFilter("Görsel Dosyaları", "*.jpeg", "*.png", "*.bmp", "*.tiff"));
        }

        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            dragAndDropField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleStartConversion() {
        String selectedInputFormat = inputFormatChoiceBox.getValue();
        String selectedOutputFormat = outputFormatChoiceBox.getValue();
        String filePath = dragAndDropField.getText();

        if (filePath.isEmpty() || selectedInputFormat == null || selectedOutputFormat == null) {
            progressListView.getItems().add("Lütfen tüm seçenekleri ve dosyayı belirleyin.");
            return;
        }

        if ("Video".equals(mediaTypeChoiceBox.getValue())) {
            String resolution = resolutionChoiceBox.getValue();
            String quality = qualityChoiceBox.getValue();
            progressListView.getItems().add("Video Dönüştürme başlatıldı: " + filePath + " -> " + selectedOutputFormat
                    + " (Çözünürlük: " + resolution + ", Kalite: " + quality + ")");
        } else if ("Görsel".equals(mediaTypeChoiceBox.getValue())) {
            String newWidth = newWidthField.getText();
            String newHeight = newHeightField.getText();
            progressListView.getItems().add("Görsel Dönüştürme başlatıldı: " + filePath + " -> " + selectedOutputFormat
                    + " (Yeni Boyut: " + newWidth + "x" + newHeight + ")");
        }
        // Burada dönüştürme işlemi başlatılacak
    }
}