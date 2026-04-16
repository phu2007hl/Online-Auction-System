package com.auction.client.controller;

import com.auction.shared.response.LoginResponse;
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
import com.auction.shared.request.Request;
import com.auction.client.service.LoginAuthenticationService;
import com.auction.client.service.RegisterAuthenticationService;
import com.auction.shared.response.Response;
import com.auction.shared.response.LoginResponse;

public class LoginController {
    private SocketClient socket;
    public void  setSocketClient(SocketClient socket){
        this.socket = socket;
    }

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        System.out.println(email);
        System.out.println(password);

        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter both username and password.");
            return;
        }
        try{
            LoginAuthenticationService service = new  LoginAuthenticationService(email,password); 
            System.out.println("Client: creating login request");
            Request request = service.createAuthRequest();
            System.out.println("Client: before sendRequest");
            LoginResponse response = (LoginResponse) socket.sendRequest(request);
            System.out.println("Client: after sendRequest");
            if (response == null){
                System.out.println("response is null");
            }
            if (response.getResponse()==true){
                switchToMain(event);
            }
            else{
                messageLabel.setText("Invalid email or password.");
            }
        }
        catch (Exception e){
            e.printStackTrace();
            //Show a text "can not connect to server" on screen
        }
        

        // Later, replace this with your real authentication class
        // Example:
        // AuthenticationService authService = new AuthenticationService();
        // boolean success = authService.login(username, password);

        System.out.println("Login email: " + email);
        System.out.println("Login password: " + password);

        
    }

    @FXML
    private void switchToRegister(ActionEvent event) {
        try {
            System.out.println("Getting ready to load register page");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RegisterView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            boolean isMaximized = stage.isMaximized();
            double width = stage.getWidth();
            double height = stage.getHeight();

            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.setMaximized(isMaximized);

            if (!isMaximized) {
                stage.centerOnScreen();
            }

            stage.setTitle("Register");
            stage.show();

            RegisterController controller = loader.getController();
            controller.setSocketClient(socket);

        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Could not open register page.");
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
@FXML
private void switchToMain(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainPageView.fxml"));
        Parent root = loader.load();

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
