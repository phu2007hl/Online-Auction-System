package com.auction.client.controller.auction;

import com.auction.client.controller.Controller;
import com.auction.client.controller.auth.LoginController;
import com.auction.client.network.SocketClient;
import com.auction.shared.request.Request;
import com.auction.shared.request.auction.CreateAuctionRequest;
import com.auction.shared.request.auction.GetApprovedAuctionListRequest;
import com.auction.shared.request.auth.LogOutRequest;
import com.auction.shared.response.auction.GetApprovedAuctionListResponse;
import com.auction.shared.response.auth.LogOutResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Controller của main page user.
*/
public class MainPageController extends Controller implements Initializable {
  private static final Logger LOGGER = LoggerFactory.getLogger(MainPageController.class);
  private static boolean updateMainPageSuccess;
  private static boolean logOutSuccess;

  private SocketClient socket;
  private Stage currentStage;
  private String currentUserName;

  @FXML
  private Label usernameLabel;

  @FXML
  private FlowPane productContainer;

  /**
  * Khởi tạo stage của main page.
  *
  * @param location vi tri FXML
  * @param resources bo tai nguyen FXML
  */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    Platform.runLater(() -> currentStage = (Stage) usernameLabel.getScene().getWindow());
  }

  /**
  * Gán socket client và tải danh sách auction đã duyệt.
  *
  * @param socket socket client
  */
  public void setSocketClient(SocketClient socket) {
    this.socket = socket;
    socket.setController(this);
    socket.startListening();
    socket.sendRequest(new GetApprovedAuctionListRequest());
  }

  /**
  * Gán tên user đang hiển thị.
  *
  * @param currentUser tên user
  */
  public void setUserName(String currentUser) {
    this.currentUserName = currentUser;
    usernameLabel.setText(currentUser);
  }

  /**
  * Thêm một auction vào giao diện main page.
  *
  * @param request request tạo auction
  */
  public void addProductBox(CreateAuctionRequest request) {
    updateMainPageSuccess = true;
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProductBox.fxml"));
      VBox productBox = loader.load();

      AuctionBoxController controller = loader.getController();
      Image image = new Image(new ByteArrayInputStream(request.getImageContent()));

      controller.setData(image, request.getStartingPrice(), request.getCategory());
      productContainer.getChildren().add(productBox);
      updateMainPageSuccess = true;
    } catch (Exception e) {
      LOGGER.error("Không thể thêm product box", e);
    }
  }

  /**
  * Chuyển sang màn tạo auction.
  *
  * @param event su kien click nut
  */
  @FXML
  private void switchToCreateAuction(ActionEvent event) {
    try {
      FXMLLoader loader =
          new FXMLLoader(getClass().getResource("/fxml/CreateAuctionPage.fxml"));
      Parent root = loader.load();

      CreateAuctionPageController controller = loader.getController();
      controller.setUserName(currentUserName);
      controller.setSocketClient(socket);

      Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
      stage.getScene().setRoot(root);
      stage.show();
    } catch (IOException e) {
      LOGGER.error("Không thể chuyển sang trang tạo auction", e);
    }
  }

  /**
  * Đăng xuất khỏi hệ thống.
  */
  @FXML
  private void switchToLogin() {
    try {
      socket.sendRequest(new LogOutRequest());
    } catch (Exception e) {
      LOGGER.error("Không thể gửi yêu cầu đăng xuất", e);
    }
  }

  @Override
  public void handle(Object obj) {
    if (obj instanceof GetApprovedAuctionListResponse) {
      GetApprovedAuctionListResponse response = (GetApprovedAuctionListResponse) obj;
      ArrayList<Request> auctionList = response.getAuctionList();
      for (Request request : auctionList) {
        addProductBox((CreateAuctionRequest) request);
      }
    } else if (obj instanceof CreateAuctionRequest) {
      CreateAuctionRequest request = (CreateAuctionRequest) obj;
      addProductBox(request);
    } else if (obj instanceof LogOutResponse) {
      LogOutResponse response = (LogOutResponse) obj;
      if (response.getResponse()) {
        logOutSuccess = true;
        try {
          FXMLLoader loader =
              new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
          Parent root = loader.load();

          LoginController controller = loader.getController();
          controller.setSocketClient(socket);

          currentStage.setScene(new Scene(root));
          currentStage.show();
        } catch (IOException e) {
          LOGGER.error("Không thể quay về trang đăng nhập", e);
        }
      }
    }
  }

  /**
  * Lấy trạng thái cập nhật main page.
  *
  * @return true nếu cập nhật thành công
  */
  public static boolean getUpdateMainSuccess() {
    return updateMainPageSuccess;
  }

  /**
  * Lấy trạng thái đăng xuất.
  *
  * @return true nếu đăng xuất thành công
  */
  public static boolean getLogOutSuccess() {
    return logOutSuccess;
  }
}
