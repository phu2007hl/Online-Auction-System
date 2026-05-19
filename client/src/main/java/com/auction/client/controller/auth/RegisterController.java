package com.auction.client.controller.auth;

import com.auction.client.controller.Controller;
import com.auction.client.controller.auction.MainPageController;
import com.auction.client.network.SocketClient;
import com.auction.client.service.RegisterAuthenticationService;
import com.auction.shared.model.User;
import com.auction.shared.request.Request;
import com.auction.shared.response.auth.RegisterResponse;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Controller cho màn hình đăng ký.
*/
public class RegisterController extends Controller implements Initializable {
  private static final Logger LOGGER = LoggerFactory.getLogger(RegisterController.class);
  private static boolean switchToMainSuccess;

  private SocketClient socket;
  private Stage currentStage;

  @FXML
  private TextField emailField;

  @FXML
  private PasswordField passwordField;

  @FXML
  private TextField usernameField;

  @FXML
  private Label messageLabel;

  /**
  * Khởi tạo stage của màn hình đăng ký.
  *
  * @param location vi tri FXML
  * @param resources bo tai nguyen FXML
  */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    Platform.runLater(() -> {
      if (emailField != null && emailField.getScene() != null) {
        currentStage = (Stage) emailField.getScene().getWindow();
      }
    });
  }

  /**
  * Gán socket client và bật listener thread.
  *
  * @param socket socket client
  */
  public void setSocketClient(SocketClient socket) {
    this.socket = socket;
    socket.setController(this);
    socket.startListening();
  }

  /**
  * Xử lý sự kiện đăng ký.
  *
  * @param event su kien click nut
  */
  @FXML
  private void handleRegister(ActionEvent event) {
    currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

    String email = emailField.getText().trim();
    String password = passwordField.getText();
    String username = usernameField.getText().trim();

    if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
      messageLabel.setText("Vui lòng nhập đầy đủ thông tin.");
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
    } catch (Exception e) {
      LOGGER.error("Không thể gửi yêu cầu đăng ký", e);
      messageLabel.setText("Không thể kết nối tới server.");
    }
  }

  /**
  * Quay về màn đăng nhập.
  *
  * @param event su kien click nut
  */
  @FXML
  private void switchToLogin(ActionEvent event) {
    currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
      Parent root = loader.load();

      LoginController controller = loader.getController();
      controller.setSocketClient(socket);

      currentStage.getScene().setRoot(root);
      currentStage.setTitle("Đăng nhập");
      currentStage.show();
    } catch (IOException e) {
      LOGGER.error("Không thể mở trang đăng nhập", e);
      messageLabel.setText("Không thể mở trang đăng nhập.");
    }
  }

  /**
  * Chuyển sang main page sau khi đăng ký thành công.
  *
  * @param currentUser tên user
  * @param valid trạng thái đăng ký
  */
  public void switchToMain(User currentUser, boolean valid) {
    switchToMainSuccess = valid;
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainPageView.fxml"));
      Parent root = loader.load();

      MainPageController controller = loader.getController();
      controller.setCurrentUser(currentUser);
      controller.setSocketClient(socket);

      currentStage.setScene(new Scene(root));
      currentStage.setTitle("Hệ thống đấu giá");
      currentStage.show();
    } catch (IOException e) {
      LOGGER.error("Không thể mở trang chính", e);
      messageLabel.setText("Không thể mở trang chính.");
    }
  }

  @Override
  public void handle(Object obj) {
    if (obj instanceof RegisterResponse) {
      RegisterResponse response = (RegisterResponse) obj;
      if (response.getResponse()) {
        switchToMain(response.getCurrentUser(), true);
      } else {
        messageLabel.setText("Email đã tồn tại.");
        switchToMainSuccess = false;
      }
    }
  }

  /**
  * Lấy trạng thái chuyển sang main page.
  *
  * @return true nếu chuyển màn thành công
  */
  public static boolean getSwitchToMainSuccess() {
    return switchToMainSuccess;
  }
}
