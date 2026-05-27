package com.auction.client.controller.auth;

import com.auction.client.controller.Controller;
import com.auction.client.controller.admin.AdminDashboardController;
import com.auction.client.controller.auction.MainPageController;
import com.auction.client.network.SocketClient;
import com.auction.client.service.LoginAuthenticationService;
import com.auction.shared.enums.LoginResponseStatus;
import com.auction.shared.model.User;
import com.auction.shared.request.Request;
import com.auction.shared.request.auth.AdminLoginRequest;
import com.auction.shared.response.auth.AdminLoginResponse;
import com.auction.shared.response.auth.LoginResponse;
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
* Controller cho màn hình đăng nhập.
*/
public class LoginController extends Controller implements Initializable {
  private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
  private static final double MAIN_PAGE_WIDTH = 1100;
  private static final double MAIN_PAGE_HEIGHT = 720;
  private static final double REGISTER_PAGE_WIDTH = 850;
  private static final double REGISTER_PAGE_HEIGHT = 550;
  private static boolean switchToAdminSuccess;
  private static boolean switchToMainSuccess;

  private SocketClient socket;
  private Stage currentStage;

  @FXML
  private TextField emailField;

  @FXML
  private PasswordField passwordField;

  @FXML
  private Label messageLabel;

  /**
  * Khởi tạo stage của màn hình đăng nhập.
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
  * Xử lý sự kiện đăng nhập.
  *
  * @param event su kien click nut
  */
  @FXML
  private void handleLogin(ActionEvent event) {
    currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

    String email = emailField.getText().trim();
    String password = passwordField.getText();

    if (email.isEmpty() || password.isEmpty()) {
      showError("Vui lòng nhập email và mật khẩu.");
      return;
    }

    try {
      if (email.equals("admin")) {
        AdminLoginRequest request = new AdminLoginRequest(password);
        socket.sendRequest(request);
        return;
      }

      LoginAuthenticationService service =
          new LoginAuthenticationService(email, password);
      Request request = service.createAuthRequest();

      if (request == null) {
        showError(service.getErrorMessage());
        return;
      }

      socket.sendRequest(request);
    } catch (Exception e) {
      LOGGER.error("Không thể gửi yêu cầu đăng nhập", e);
      showError("Không thể kết nối tới server.");
    }
  }

  /**
  * Chuyển sang màn đăng ký.
  *
  * @param event su kien click nut
  */
  @FXML
  private void switchToRegister(ActionEvent event) {
    currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RegisterView.fxml"));
      Parent root = loader.load();

      RegisterController controller = loader.getController();
      controller.setSocketClient(socket);

      currentStage.getScene().setRoot(root);
      currentStage.setTitle("Đăng ký");
      currentStage.setWidth(REGISTER_PAGE_WIDTH);
      currentStage.setHeight(REGISTER_PAGE_HEIGHT);
      currentStage.centerOnScreen();
      currentStage.show();
    } catch (IOException e) {
      LOGGER.error("Không thể mở trang đăng ký", e);
      showError("Không thể mở trang đăng ký.");
    }
  }

  /**
  * Chuyển sang main page sau khi đăng nhập user thành công.
  *
  * @param currentUser user hiện tại
  * @param valid trạng thái đăng nhập
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
      currentStage.setWidth(MAIN_PAGE_WIDTH);
      currentStage.setHeight(MAIN_PAGE_HEIGHT);
      currentStage.centerOnScreen();
      currentStage.show();
    } catch (IOException e) {
      LOGGER.error("Không thể mở trang chính", e);
      showError("Không thể mở trang chính.");
    }
  }

  /**
  * Chuyển sang admin dashboard sau khi đăng nhập admin thành công.
  *
  * @param valid trạng thái đăng nhập
  */
  public void switchToAdminDashboard(boolean valid) {
    switchToAdminSuccess = valid;
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
      Parent root = loader.load();

      AdminDashboardController controller = loader.getController();
      controller.setSocketClient(socket);

      currentStage.setScene(new Scene(root));
      currentStage.setTitle("Bảng điều khiển admin");
      currentStage.show();
    } catch (Exception e) {
      LOGGER.error("Không thể mở trang admin", e);
      showError("Không thể mở trang admin.");
    }
  }

  /**
  * Lấy email đăng nhập.
  *
  * @return email đăng nhập
  */
  public String getEmailInput() {
    return emailField.getText().trim();
  }

  /**
  * Lấy mật khẩu đăng nhập.
  *
  * @return mật khẩu đăng nhập
  */
  public String getPasswordInput() {
    return passwordField.getText();
  }

  /**
  * Xóa dữ liệu đăng nhập trên form.
  */
  public void clearFields() {
    emailField.clear();
    passwordField.clear();
    messageLabel.setText("");
  }

  @Override
  public void handle(Object obj) {
    if (obj instanceof AdminLoginResponse) {
      AdminLoginResponse response = (AdminLoginResponse) obj;
      if (response.getResponse()) {
        switchToAdminDashboard(true);
      } else {
        switchToAdminSuccess = false;
        showError("Đăng nhập admin thất bại");
      }
    } else if (obj instanceof LoginResponse) {
      LoginResponse response = (LoginResponse) obj;
      if (response.getResponse()) {
        switchToMain(response.getCurrentUser(), true);
      } else {
        if (response.getStatus() == LoginResponseStatus.INVALID_PASSWORD) {
          showError("Mật khẩu không hợp lệ");
        }
        if (response.getStatus() == LoginResponseStatus.EMAIL_NOT_FOUND) {
          showError("Email không tồn tại");
        }
        switchToMainSuccess = false;
      }
    }
  }

  private void showError(String message) {
    messageLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
    messageLabel.setText(message);
  }

  /**
  * Lấy trạng thái chuyển sang admin dashboard.
  *
  * @return true nếu chuyển màn thành công
  */
  public static boolean getSwitchToAdminSuccess() {
    return switchToAdminSuccess;
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
