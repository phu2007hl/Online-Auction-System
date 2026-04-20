package com.auction.client.controller;

import com.auction.client.network.SocketClient;
import com.auction.shared.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLOutput;
import java.time.LocalDate;

public class CreateAuctionPageController {

    private SocketClient socket;
    private User currentUser;

    @FXML private TextField productNameField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextField startingPriceField;
    @FXML private DatePicker endDatePicker;
    @FXML private TextArea descriptionArea;
    @FXML private Label imagePathLabel;

    @FXML private Label messageLabel;

    private File selectedImageFile;
    private byte[] imageContent;

    @FXML
    public void handleUploadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh sản phẩm");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        // show Dialog
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            imagePathLabel.setText(file.getName());
            try {
                // Lưu ảnh dưới dạng byte
                imageContent = Files.readAllBytes(file.toPath());
                System.out.println("Lưu ảnh thành công");
            }
            catch (IOException e) {
                System.out.println("Không thể lưu ảnh");
            }
        }
    }

    @FXML
    public void handleCreateAuction(ActionEvent event) {
        String productName = productNameField.getText().trim();  // Tên sản phẩm
        String category = categoryComboBox.getValue();           // Danh mục sản phẩm
        String startingPrice = startingPriceField.getText().trim();   // Giá khởi điểm
        LocalDate endDate = endDatePicker.getValue();       // Ngày kết thúc
        String description = descriptionArea.getText().trim();      //  Mô tả


        System.out.println("Tạo đấu giá thành công: " + productNameField.getText());
    }

    @FXML
    private void switchToMain(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainPageView.fxml"));
            Parent root = loader.load();

            MainPageController controller = loader.getController();

            if (this.currentUser != null) {
                controller.setUserName(this.currentUser.getUsername());
            }
            if (this.socket != null) {
                controller.setSocketClient(this.socket);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Auction System");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not open main page.");
        }
    }
}