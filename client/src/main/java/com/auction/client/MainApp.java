package com.auction.client;

import com.auction.client.controller.LoginController;
import com.auction.client.network.SocketClient;
import com.auction.server.database.AuctionListDatabase;
import com.auction.server.database.UserDatabase;
import com.auction.server.database.createAuctionDatabase;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            UserDatabase.setPath("User.ser");
            createAuctionDatabase.setPath("AuctionRequest.ser");
            AuctionListDatabase.setPath("AuctionList.ser");

            SocketClient socketClient = new SocketClient(4100);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();

            LoginController controller = loader.getController();
            controller.setSocketClient(socketClient);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Auction System - Login");
            
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
