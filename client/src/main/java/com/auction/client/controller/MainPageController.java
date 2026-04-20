package com.auction.client.controller;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

import com.auction.client.network.SocketClient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

public class MainPageController {
    private SocketClient socket;
    @FXML
    private Label usernameLabel;
    public void setSocketClient(SocketClient socket){
        this.socket = socket;
    }
    public void setUserName(String currentUser){
        usernameLabel.setText(currentUser);
    }
    @FXML
    private void switchToCreateAuction(ActionEvent event){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CreateAuctionPage.fxml"));
        try{
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.show();

        }
        catch (IOException e){
            e.printStackTrace();
            System.out.println("Can't find fxml file");
        }

    }
    @FXML
    private void switchToLogin(ActionEvent event){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
        try{
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            LoginController controller = loader.getController();
            controller.setSocketClient(socket);
            stage.getScene().setRoot(root);
            stage.setTitle("Auction System - Login");
            stage.show();


        }
        catch (IOException e){
            e.printStackTrace();
            System.out.println("Cant' switch back to Login Page");
        }


    }
    
    
}
