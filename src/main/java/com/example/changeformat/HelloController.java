package com.example.changeformat;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import java.io.BufferedReader;
import java.io.InputStreamReader;

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
            inputFormatChoiceBox.setItems(FXCollections.observableArrayList("mp4", "avi", "mov", "flv"));
            outputFormatChoiceBox.setItems(FXCollections.observableArrayList("mp4", "avi", "mov", "flv"));
            videoOptions.setVisible(true);
            imageOptions.setVisible(false);
            setVideoOptions();
        } else if ("Görsel".equals(mediaType)) {
            inputFormatChoiceBox.setItems(FXCollections.observableArrayList("jpeg", "png", "bmp", "tiff","jpg", "webp"));
            outputFormatChoiceBox.setItems(FXCollections.observableArrayList("jpeg", "png", "bmp", "tiff", "webp","jpg"));
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
                    ExtensionFilter("Video Dosyaları", "*.mp4", "*.avi", "*.flv", "*.mov"));
        } else if ("Görsel".equals(mediaTypeChoiceBox.getValue())) {
            fileChooser.getExtensionFilters().add(new FileChooser.
                    ExtensionFilter("Görsel Dosyaları", "*.jpeg", "*.png", "*.bmp", "*.tiff", "*.jpg", "*.webp"));
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

        String outputFilePath = generateOutputFilePath(filePath, selectedOutputFormat);

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

        convertMediaFile(filePath, outputFilePath, selectedOutputFormat);
    }

    private String generateOutputFilePath(String inputFilePath, String outputFormat) {
        String userDesktop = System.getProperty("user.home") + File.separator + "Masaüstü"; // Masaüstü yolu
        String baseName = new File(inputFilePath).getName().replaceFirst("[.][^.]+$", ""); // Dosya adından uzantıyı çıkar
        String outputFilePath = userDesktop + File.separator + baseName + "_converted." + outputFormat.toLowerCase();
        int counter = 1;

        // Dosya mevcutsa yeni ad oluştur
        while (new File(outputFilePath).exists()) {
            outputFilePath = userDesktop + File.separator + baseName + "_converted(" + counter + ")." + outputFormat.toLowerCase();
            counter++;
        }

        return outputFilePath;
    }

    @FXML
    private void convertMediaFile(String inputFilePath, String outputFilePath, String format) {
        try {
            String ffmpegCommand;

            // Medya türüne göre komutu oluştur
            if (outputFilePath.endsWith(".png") || outputFilePath.endsWith(".jpeg") || outputFilePath.endsWith(".bmp")) {
                // Görsel için boyut değiştirme
                String newWidth = newWidthField.getText().trim();
                String newHeight = newHeightField.getText().trim();
                if (!newWidth.isEmpty() && !newHeight.isEmpty()) {
                    ffmpegCommand = String.format("ffmpeg -i \"%s\" -vf scale=%s:%s \"%s\"",
                            inputFilePath, newWidth, newHeight, outputFilePath);
                } else {
                    ffmpegCommand = String.format("ffmpeg -i \"%s\" \"%s\"", inputFilePath, outputFilePath);
                }
            } else {
                // Video için çözünürlük ve kalite
                String resolution = resolutionChoiceBox.getValue();
                String quality = qualityChoiceBox.getValue();

                // Çözünürlük parametreleri
                String[] resParts = resolution.split("x");
                String width = resParts[0];
                String height = resParts[1];

                // Kalite parametreleri
                String bitrate = "3M"; // Varsayılan
                if ("Yüksek".equals(quality)) {
                    bitrate = "5M";
                } else if ("Orta".equals(quality)) {
                    bitrate = "3M";
                } else if ("Düşük".equals(quality)) {
                    bitrate = "1M";
                }

                ffmpegCommand = String.format("ffmpeg -i \"%s\" -vf scale=%s:%s -b:v %s \"%s\"",
                        inputFilePath, width, height, bitrate, outputFilePath);
            }

            // Komutu çalıştır
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", ffmpegCommand);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Çıktıyı oku (isteğe bağlı)
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // İşlemin tamamlanmasını bekle
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Dönüştürme başarıyla tamamlandı!");
                progressListView.getItems().add("Dönüştürme başarıyla tamamlandı: " + outputFilePath);
            } else {
                System.out.println("Dönüştürme sırasında bir hata oluştu.");
                progressListView.getItems().add("Dönüştürme sırasında bir hata oluştu: " + inputFilePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
            progressListView.getItems().add("Dönüştürme işlemi başarısız oldu: " + e.getMessage());
        }
    }
}
