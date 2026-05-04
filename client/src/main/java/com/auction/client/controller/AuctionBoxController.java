package com.auction.client.controller;

import com.auction.client.network.SocketClient;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AuctionBoxController {
    private SocketClient socket;

    @FXML
    private ImageView imageView;

    @FXML
    private Label priceLabel;

    @FXML
    private Label categoryLabel;


    // Set data method
    public void setData(Image image, String startingPrice, String category) {
        imageView.setImage(image);
        priceLabel.setText("Price: $" + startingPrice);
        categoryLabel.setText("Category: " + category);
    }
    public void  setSocketClient(SocketClient socket){
        this.socket = socket;
    }
}