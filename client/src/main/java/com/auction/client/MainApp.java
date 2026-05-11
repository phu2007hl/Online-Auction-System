package com.auction.client;

import com.auction.client.controller.auth.LoginController;
import com.auction.client.network.SocketClient;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Điểm khởi động của ứng dụng client.
*/
public class MainApp extends Application {
  private static final Logger LOGGER = LoggerFactory.getLogger(MainApp.class);

  /**
  * Khởi động màn hình đăng nhập đầu tiên.
  *
  * @param primaryStage cửa sổ chính của JavaFX
  */
  @Override
  public void start(Stage primaryStage) {
    try {
      SocketClient socketClient = new SocketClient(4100);
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
      Parent root = loader.load();

      LoginController controller = loader.getController();
      controller.setSocketClient(socketClient);

      Scene scene = new Scene(root);
      primaryStage.setScene(scene);
      primaryStage.setTitle("Hệ thống đấu giá - Đăng nhập");
      primaryStage.show();
    } catch (Exception e) {
      LOGGER.error("Không thể khởi động ứng dụng", e);
    }
  }

  /**
  * Hàm main của ứng dụng.
  *
  * @param args tham số dòng lệnh
  */
  public static void main(String[] args) {
    launch(args);
  }
}
