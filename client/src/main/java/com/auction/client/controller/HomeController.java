package com.auction.client.controller;

import com.auction.client.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class HomeController {

    @FXML
    private StackPane contentArea;

    /**
     * Hàm dùng để nạp một file FXML vào vùng giữa của trang chủ
     */
    private void changeView(String fxml) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/fxml/" + fxml + ".fxml"));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showAuctionList() {
        changeView("auction-list");
    }

    @FXML
    private void showCategories() {
        // changeView("categories"); // Tạo file categories.fxml sau
    }

    @FXML
    private void showSellProduct() {
        // changeView("sell-product"); // Tạo file sell-product.fxml sau
    }

    @FXML
    private void handleViewProfile() {
        // changeView("profile");
    }

    @FXML
    private void handleLogout() {
        try {
            MainApp.setRoot("login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}