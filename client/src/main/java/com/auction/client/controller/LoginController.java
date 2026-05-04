package com.auction.client.controller;

import com.auction.client.network.SocketClient;
import com.auction.client.service.LoginAuthenticationService;
import com.auction.shared.model.User;
import com.auction.shared.request.AdminLoginRequest;
import com.auction.shared.request.Request;
import com.auction.shared.request.createAuctionRequest;
import com.auction.shared.response.AdminLoginResponse;
import com.auction.shared.response.LoadAuctionResponse;
import com.auction.shared.response.LoginResponse;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class LoginController extends Controller implements Initializable {

    private SocketClient socket;
    private Stage currentStage;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            if (emailField != null && emailField.getScene() != null) {
                currentStage = (Stage) emailField.getScene().getWindow();
            }
        });
    }

    public void setSocketClient(SocketClient socket) {
        this.socket = socket;
        socket.setController(this);
        socket.startListening();
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Vui lòng nhập email và mật khẩu.");
            return;
        }

        try {
            if (email.equals("admin")) {
                AdminLoginRequest request = new AdminLoginRequest(password);
                socket.sendRequest(request);
                return;
            }

            LoginAuthenticationService service =
                    new LoginAuthenticationService(email, password);

            Request request = service.createAuthRequest();

            if (request == null) {
                messageLabel.setText(service.getErrorMessage());
                return;
            }

            socket.sendRequest(request);

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Cannot connect to server.");
        }
    }

    @FXML
    private void switchToRegister(ActionEvent event) {
        currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/RegisterView.fxml")
            );

            Parent root = loader.load();

            RegisterController controller = loader.getController();
            controller.setSocketClient(socket);

            currentStage.getScene().setRoot(root);
            currentStage.setTitle("Register");
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Could not open register page.");
        }
    }

    public void switchToMain(User currentUser) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/MainPageView.fxml")
            );

            Parent root = loader.load();

            MainPageController controller = loader.getController();
            controller.setUserName(currentUser.getUsername());
            controller.setSocketClient(socket);

            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Auction System");
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Could not open main page.");
        }
    }

    public void switchToAdminDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/AdminDashboard.fxml")
            );

            Parent root = loader.load();

            AdminDashboardController controller = loader.getController();
            controller.setSocketClient(socket);

            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Admin Dashboard");
            currentStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Could not open admin page.");
        }
    }

    public String getEmailInput() {
        return emailField.getText().trim();
    }

    public String getPasswordInput() {
        return passwordField.getText();
    }

    public void clearFields() {
        emailField.clear();
        passwordField.clear();
        messageLabel.setText("");
    }

    public void handle(Object obj){
        if (obj instanceof AdminLoginResponse){
            AdminLoginResponse response = (AdminLoginResponse) obj;
            if (response.getResponse()){
                switchToAdminDashboard();
            }
        }
        else if (obj instanceof LoginResponse){
            LoginResponse response = (LoginResponse) obj;
            if (response.getResponse()){
                switchToMain(response.getCurrentUser());
            }
        }
    }
}
