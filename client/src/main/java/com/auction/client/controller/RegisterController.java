package com.auction.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import com.auction.client.MainApp;
import java.io.IOException;

public class RegisterController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox agreeCheckBox;
    @FXML private Label errorLabel;

    @FXML private Canvas captchaCanvas;
    @FXML private TextField captchaInputField;

    private String currentCaptchaText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Tự động tạo mã CAPTCHA khi màn hình đăng ký hiện lên
        generateCaptcha();
    }

    /**
     * Xử lý khi người dùng nhấn nút Đăng ký
     */
    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String captchaInput = captchaInputField.getText();

        // 1. Kiểm tra các trường trống
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || captchaInput.isEmpty()) {
            showError("Vui lòng điền đầy đủ thông tin!");
            return;
        }

        // 2. Kiểm tra CAPTCHA
        if (!captchaInput.equalsIgnoreCase(currentCaptchaText)) {
            showError("Mã xác nhận không đúng!");
            generateCaptcha(); // Đổi mã mới ngay lập tức
            return;
        }

        // 3. Kiểm tra Checkbox điều khoản
        if (!agreeCheckBox.isSelected()) {
            showError("Bạn phải đồng ý với điều khoản dịch vụ!");
            return;
        }

        // Nếu mọi thứ ổn
        errorLabel.setStyle("-fx-text-fill: green;");
        errorLabel.setText("Đăng ký thành công! Đang chuyển hướng...");

        // TODO: Gửi dữ liệu qua Socket/API lên Server để lưu vào database
        System.out.println("Gửi yêu cầu đăng ký: " + username + " - " + email);
    }

    /**
     * Xử lý khi click vào Canvas để đổi mã CAPTCHA mới
     */
    @FXML
    private void reloadCaptcha(MouseEvent event) {
        generateCaptcha();
    }

    /**
     * Thuật toán vẽ CAPTCHA trực tiếp lên Canvas
     */
    private void generateCaptcha() {
        GraphicsContext gc = captchaCanvas.getGraphicsContext2D();
        Random rand = new Random();

        // Xóa Canvas và tô nền
        gc.clearRect(0, 0, captchaCanvas.getWidth(), captchaCanvas.getHeight());
        gc.setFill(Color.web("#f4f4f4"));
        gc.fillRect(0, 0, captchaCanvas.getWidth(), captchaCanvas.getHeight());

        // Tạo chuỗi ngẫu nhiên 5 ký tự
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(chars.charAt(rand.nextInt(chars.length())));
        }
        currentCaptchaText = sb.toString();

        // Vẽ chữ với hiệu ứng nghiêng nhẹ
        gc.setFont(Font.font("Verdana", 26));
        for (int i = 0; i < currentCaptchaText.length(); i++) {
            gc.setFill(Color.rgb(rand.nextInt(150), rand.nextInt(150), rand.nextInt(150)));
            // Vẽ từng ký tự với vị trí x cách nhau, y ngẫu nhiên một chút
            gc.fillText(String.valueOf(currentCaptchaText.charAt(i)), 20 + (i * 25), 35 + rand.nextInt(10) - 5);
        }

        // Vẽ thêm các đường kẻ gây nhiễu
        for (int i = 0; i < 5; i++) {
            gc.setStroke(Color.rgb(rand.nextInt(200), rand.nextInt(200), rand.nextInt(200)));
            gc.strokeLine(rand.nextInt(150), rand.nextInt(50), rand.nextInt(150), rand.nextInt(50));
        }
    }

    @FXML
    private void handleGoToLogin() {
        try {
            // Chuyển về màn hình login.fxml
            MainApp.setRoot("login");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Không thể quay lại trang đăng nhập!");
        }
    }

    private void showError(String message) {
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setText(message);
    }
}