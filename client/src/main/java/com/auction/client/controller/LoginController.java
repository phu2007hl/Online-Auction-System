package com.auction.client.controller;

import com.auction.client.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    // Các biến cũ của bạn
    @FXML private TextField tfUsername;
    @FXML private PasswordField pfPassword;
    @FXML private Label lblStatus;

    // Các biến mới cho CAPTCHA (Cần kéo thả Canvas và TextField vào login.fxml)
    @FXML private Canvas captchaCanvas;
    @FXML private TextField tfCaptcha;

    private String currentCaptchaText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        generateCaptcha(); // Tạo mã ngay khi mở form
    }

    @FXML
    private void handleLogin() {
        String username = tfUsername.getText().trim();
        String password = pfPassword.getText().trim();
        String captcha = "";

        // Tránh lỗi NullPointer nếu bạn chưa thêm tfCaptcha vào giao diện
        if (tfCaptcha != null) {
            captcha = tfCaptcha.getText().trim();
        }

        // 1. Kiểm tra rỗng
        if (username.isEmpty() || password.isEmpty() || (tfCaptcha != null && captcha.isEmpty())) {
            showError("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        // 2. Kiểm tra CAPTCHA
        if (tfCaptcha != null && !captcha.equalsIgnoreCase(currentCaptchaText)) {
            showError("Mã xác nhận không đúng!");
            generateCaptcha(); // Đổi mã mới
            return;
        }

        // 3. Logic Đăng nhập (Tạm thời hardcode)
        if (username.equals("admin") && password.equals("1234")) {
            showSuccess("Đăng nhập thành công! Chào " + username);
            // TODO: Chuyển sang màn hình chính (auction-list.fxml)

            try {
                // Gọi hàm setRoot từ MainApp để đổi giao diện
                MainApp.setRoot("home");
            } catch (IOException e) {
                e.printStackTrace();
                showError("Không thể tải màn hình đăng ký!");
            }

        } else {
            showError("Sai tên đăng nhập hoặc mật khẩu!");
            generateCaptcha(); // Sai pass cũng nên đổi mã bảo mật
        }
    }

    /**
     * Sự kiện gắn vào nút "Đăng ký" trên màn hình Login
     */
    @FXML
    private void handleGoToRegister() {
        try {
            // Gọi hàm setRoot từ MainApp để đổi giao diện
            MainApp.setRoot("register");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Không thể tải màn hình đăng ký!");
        }
    }

    // --- Các hàm hỗ trợ ---

    @FXML
    private void reloadCaptcha(MouseEvent event) {
        generateCaptcha();
    }

    private void generateCaptcha() {
        if (captchaCanvas == null) return; // Tránh lỗi nếu chưa thiết kế UI

        GraphicsContext gc = captchaCanvas.getGraphicsContext2D();
        Random rand = new Random();

        gc.clearRect(0, 0, captchaCanvas.getWidth(), captchaCanvas.getHeight());
        gc.setFill(Color.web("#e8e8e8"));
        gc.fillRect(0, 0, captchaCanvas.getWidth(), captchaCanvas.getHeight());

        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(chars.charAt(rand.nextInt(chars.length())));
        }
        currentCaptchaText = sb.toString();

        gc.setFont(Font.font("Arial", 24));
        for (int i = 0; i < currentCaptchaText.length(); i++) {
            gc.setFill(Color.rgb(rand.nextInt(100), rand.nextInt(100), rand.nextInt(100)));
            gc.fillText(String.valueOf(currentCaptchaText.charAt(i)), 20 + (i * 22), 35 + rand.nextInt(10) - 5);
        }

        for (int i = 0; i < 5; i++) {
            gc.setStroke(Color.rgb(rand.nextInt(200), rand.nextInt(200), rand.nextInt(200)));
            gc.strokeLine(rand.nextInt(150), rand.nextInt(50), rand.nextInt(150), rand.nextInt(50));
        }
    }

    private void showError(String msg) {
        lblStatus.setText(msg);
        lblStatus.setStyle("-fx-text-fill: red;");
    }

    private void showSuccess(String msg) {
        lblStatus.setText(msg);
        lblStatus.setStyle("-fx-text-fill: green;");
    }
}