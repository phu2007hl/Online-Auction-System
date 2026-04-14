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
        scene.setRoot(loadFXML(fxml));


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