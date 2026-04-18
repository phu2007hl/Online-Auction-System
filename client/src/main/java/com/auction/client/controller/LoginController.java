package com.auction.client.controller;

import com.auction.shared.enums.LoginResponseStatus;
import com.auction.shared.model.User;
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
            messageLabel.setText("Vui lòng nhập email và mật khẩu.");
            return;
        }
        try{
            LoginAuthenticationService service = new  LoginAuthenticationService(email,password); 
            System.out.println("Client: creating login request");
            Request request = service.createAuthRequest();

            if (request == null) {
                String errorMessage = service.getErrorMessage();
                messageLabel.setText(errorMessage);
                System.out.println("Client: Can not create login request because of error");
                return;
            }

            System.out.println("Client: before sendRequest");
            LoginResponse response = (LoginResponse) socket.sendRequest(request);

            System.out.println("Client: after sendRequest");

            if (response.getResponse()){
                String userName = socket.getCurrentUser().getUsername();
                switchToMain(event,userName);
            }
            else{
                if(response.getStatus()==LoginResponseStatus.EMAIL_NOT_FOUND){
                    messageLabel.setText("Email không tồn tại.");
                }
                else if(response.getStatus()==LoginResponseStatus.INVALID_PASSWORD)
                messageLabel.setText("Mật khẩu không hợp lệ.");
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
            
            stage.getScene().setRoot(root);

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
private void switchToMain(ActionEvent event,String currentUser) {
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
