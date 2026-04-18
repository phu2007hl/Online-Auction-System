package com.auction.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

import com.auction.client.network.SocketClient;
import com.auction.shared.model.User;
import com.auction.shared.request.Request;
import com.auction.client.service.RegisterAuthenticationService;
import com.auction.shared.response.RegisterResponse;

public class RegisterController {
    private SocketClient socket;

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

    @FXML
    public void setSocketClient(SocketClient socket){
        this.socket = socket;

    }
    @FXML
    private void handleRegister(ActionEvent event) {
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
        try{
            RegisterAuthenticationService service = new RegisterAuthenticationService(email, password, username);
            System.out.println("Client: creating register request");
            Request request = service.createAuthRequest();


            if (request == null) {
                String errorMessage = service.getErrorMessage();
                messageLabel.setText(errorMessage);
                System.out.println("Client: Can not create register request because of error");
                return;
            }


            System.out.println("Client: before sendRequest");
            RegisterResponse response = (RegisterResponse) socket.sendRequest(request);

            System.out.println("Client: after sendRequest");

            if (response.getResponse() == true){
                messageLabel.setText("Đăng ký thành công. Đang chuyển trang...");
                String  userName = socket.getCurrentUser().getUsername();
                switchToMain(event,userName);
            }
            else{
                messageLabel.setText("Email đã tồn tại.");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        // Later, replace this with your real authentication class
        // Example:
        // AuthenticationService authService = new AuthenticationService();
        // boolean success = authService.register(fullName, username, email, password);

        System.out.println("Full name: " + fullName);
        System.out.println("Username: " + username);
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);

    }

    @FXML
    private void switchToLogin(ActionEvent event) {
        try {
            System.out.println("Getting ready to load login page");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            stage.getScene().setRoot(root);

            stage.setTitle("Login");

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Could not open login page.");
        }
    }

    public String getFullNameInput() {
        return fullNameField.getText().trim();
    }

    public String getUsernameInput() {
        return usernameField.getText().trim();
    }

    public String getEmailInput() {
        return emailField.getText().trim();
    }

    public String getPasswordInput() {
        return passwordField.getText();
    }

    public String getConfirmPasswordInput() {
        return confirmPasswordField.getText();
    }

    public void clearFields() {
        fullNameField.clear();
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        messageLabel.setText("");
    }
@FXML
private void switchToMain(ActionEvent event, String currentUser) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainPageView.fxml"));
        Parent root = loader.load();
        MainPageController controller = loader.getController();
        controller.setUserName(currentUser);
        controller.setSocketClient(socket);

        // Get controller of MainPage
        

        // Switch scene
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Auction System");
        stage.show();

    } catch (IOException e) {
        e.printStackTrace();
        messageLabel.setText("Could not open main page.");
    }
}
}
