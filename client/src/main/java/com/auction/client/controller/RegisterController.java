package com.auction.client.controller;

import com.auction.client.network.SocketClient;
import com.auction.client.service.RegisterAuthenticationService;
import com.auction.shared.model.User;
import com.auction.shared.request.Request;
import com.auction.shared.response.RegisterResponse;

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
import java.util.ResourceBundle;

public class RegisterController extends Controller implements Initializable {

    private SocketClient socket;
    private Stage currentStage;
    private User currentUser;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            currentStage = (Stage) emailField.getScene().getWindow();
        });
    }

    public void setSocketClient(SocketClient socket) {
        this.socket = socket;
        socket.setController(this);
        socket.startListening();
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("Vui lòng điền đầy đủ thông tin.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Mật khẩu không khớp.");
            return;
        }

        try {
            RegisterAuthenticationService service =
                    new RegisterAuthenticationService(email, password, username);

            Request request = service.createAuthRequest();

            if (request == null) {
                messageLabel.setText(service.getErrorMessage());
                return;
            }

            socket.sendRequest(request);
            System.out.println("Request sent");

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Cannot connect to server.");
        }
    }

    @FXML
    private void switchToLogin(ActionEvent event) {
        currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/LoginView.fxml")
            );

            Parent root = loader.load();

            LoginController controller = loader.getController();
            controller.setSocketClient(socket);

            currentStage.getScene().setRoot(root);
            currentStage.setTitle("Login");
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Could not open login page.");
        }
    }

    public void switchToMain(String currentUser) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/MainPageView.fxml")
            );

            Parent root = loader.load();

            MainPageController controller = loader.getController();
            controller.setUserName(currentUser);
            controller.setSocketClient(socket);

            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Auction System");
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Could not open main page.");
        }
    }
    public void handle(Object obj){
        if (obj instanceof RegisterResponse){
            RegisterResponse response = (RegisterResponse) obj;
            if (response.getResponse()){
                switchToMain(response.getCurrentUser().getUsername());
            }
        }
        
    }
}
