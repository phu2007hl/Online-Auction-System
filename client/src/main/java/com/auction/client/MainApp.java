package com.auction.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    // Biến scene được đặt làm static để dùng chung cho toàn bộ ứng dụng
    private static Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Tải file login.fxml làm màn hình mặc định khi mở app
        scene = new Scene(loadFXML("login"), 400, 600);

        primaryStage.setTitle("Auction System - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Hàm tĩnh dùng để chuyển màn hình từ bất kỳ Controller nào
     * Ví dụ: MainApp.setRoot("register");
     */
    public static void setRoot(String fxml) throws IOException {
        // 1. Nạp và thay đổi nội dung giao diện
        scene.setRoot(loadFXML(fxml));

        // 2. Lấy ra đối tượng Stage (cửa sổ ứng dụng) hiện hành
        Stage stage = (Stage) scene.getWindow();

        // 3. Kiểm tra màn hình đích để điều chỉnh kích thước
        if (fxml.equals("home")) {
            // Phóng to toàn màn hình (Maximized - vẫn giữ thanh taskbar của Windows)
            stage.setMaximized(true);

            // Gợi ý: Nếu bạn muốn Full-screen thực sự (che luôn cả thanh taskbar ở dưới đáy),
            // bạn có thể dùng: stage.setFullScreen(true);
        }
        else if (fxml.equals("login") || fxml.equals("register")) {
            // Nếu quay lại trang đăng nhập/đăng ký thì tắt chế độ phóng to và đưa về kích thước cũ
            stage.setMaximized(false);
            stage.setWidth(400);
            stage.setHeight(600);
            stage.centerOnScreen(); // Căn giữa cửa sổ ra giữa màn hình
        }
    }

    /**
     * Hàm hỗ trợ nạp file FXML
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/" + fxml + ".fxml"));
        return loader.load();
    }


    public static void main(String[] args) {
        launch(args);
    }
}