package com.auction.client.controller;

import com.auction.client.network.SocketClient;
import com.auction.shared.request.LoadAuctionRequest;
import com.auction.shared.request.LogOutRequest;
import com.auction.shared.request.Request;
import com.auction.shared.request.createAuctionRequest;
import com.auction.shared.response.LoadAuctionResponse;
import com.auction.shared.response.LogOutResponse;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainPageController extends Controller implements Initializable {

    private SocketClient socket;
    private Stage currentStage;

    @FXML
    private Label usernameLabel;

    @FXML
    private FlowPane productContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() ->
                currentStage = (Stage) usernameLabel.getScene().getWindow()
        );
    }

    public void setSocketClient(SocketClient socket) {
        this.socket = socket;
        socket.setController(this);
        socket.startListening();

        // Load all auctions when entering main page
        socket.sendRequest(new LoadAuctionRequest());
    }

    public void setUserName(String currentUser) {
        usernameLabel.setText(currentUser);
    }

    public void addProductBox(createAuctionRequest request) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/ProductBox.fxml")
            );

            VBox productBox = loader.load();

            AuctionBoxController controller = loader.getController();

            Image image = new Image(
                    new ByteArrayInputStream(request.getImageContent())
            );

            controller.setData(
                    image,
                    request.getStartingPrice(),
                    request.getCategory()
            );

            productContainer.getChildren().add(productBox);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToCreateAuction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/CreateAuctionPage.fxml")
            );

            Parent root = loader.load();

            CreateAuctionPageController controller = loader.getController();
            controller.setSocketClient(socket);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cannot switch to Create Auction Page");
        }
    }

    @FXML
    private void switchToLogin() {
        try {
            socket.sendRequest(new LogOutRequest());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handle(Object obj) {

        if (obj instanceof LoadAuctionResponse) {
            LoadAuctionResponse response = (LoadAuctionResponse) obj;

            ArrayList<Request> auctionList = response.getAuctionList();

            for (Request request : auctionList) {
                System.out.println(request.getClass());
                addProductBox((createAuctionRequest) request);
            }
        }

        else if (obj instanceof createAuctionRequest) {
            createAuctionRequest request = (createAuctionRequest) obj;
            addProductBox(request);
        }

        else if (obj instanceof LogOutResponse) {
            LogOutResponse response = (LogOutResponse) obj;

            if (response.getResponse()) {
                try {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/fxml/LoginView.fxml")
                    );

                    Parent root = loader.load();

                    LoginController controller = loader.getController();
                    controller.setSocketClient(socket);

                    currentStage.setScene(new Scene(root));
                    currentStage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Cannot switch back to Login Page");
                }
            }
        }
    }
}
