package com.auction.client.controller.auction;

import com.auction.client.controller.Controller;
import com.auction.client.controller.auth.LoginController;
import com.auction.client.network.SocketClient;
import com.auction.shared.auction.Auction;
import com.auction.shared.request.Request;
import com.auction.shared.request.auction.CreateAuctionRequest;
import com.auction.shared.request.auction.GetApprovedAuctionListRequest;
import com.auction.shared.request.auction.JoinRoomRequest;
import com.auction.shared.request.auth.LogOutRequest;
import com.auction.shared.response.auction.GetApprovedAuctionListResponse;
import com.auction.shared.response.auction.JoinRoomResponse;
import com.auction.shared.response.auth.LogOutResponse;
import com.auction.shared.model.User;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Controller của main page user.
*/
public class MainPageController extends Controller implements Initializable {
  private static final Logger LOGGER = LoggerFactory.getLogger(MainPageController.class);
  private static final double LOGIN_PAGE_WIDTH = 924;
  private static final double LOGIN_PAGE_HEIGHT = 500;
  private static final double AUCTION_DETAIL_WIDTH = 1440;
  private static final double AUCTION_DETAIL_HEIGHT = 820;
  private static boolean updateMainPageSuccess;
  private static boolean logOutSuccess;

  private SocketClient socket;
  private Stage currentStage;
  private String currentUserName;
  private User currentUser;
  private int pendingAuctionId;
  private final Set<Integer> displayedAuctionIds = new HashSet<>();

  @FXML
  private Label usernameLabel;

  @FXML
  private VBox productContainer;

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
    requestApprovedAuctionList();
  }

  /**
  * Gửi request lấy lại danh sách auction đã duyệt.
  */
  private void requestApprovedAuctionList() {
    socket.sendRequest(new GetApprovedAuctionListRequest());
    LOGGER.info("Đã gửi GetApprovedAuctionListRequest");
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
  * Gán user hiện tại.
  *
  * @param currentUser user hiện tại
  */
  public void setCurrentUser(User currentUser) {
    this.currentUser = currentUser;
    if (currentUser != null) {
      setUserName(currentUser.getUsername());
    }
  }

  /**
  * Thêm một auction vào giao diện main page.
  *
  * @param request request tạo auction
  */
  public void addProductBox(int auctionId, Auction auction) {
    if (displayedAuctionIds.contains(auctionId)) {
      LOGGER.info("Bỏ qua auction đã hiển thị [auctionId: {}]", auctionId);
      return;
    }
    updateMainPageSuccess = true;
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProductBox.fxml"));
      Node productBox = loader.load();

      AuctionBoxController controller = loader.getController();
      Image image = null;
      if (auction.getImageContent() != null && auction.getImageContent().length > 0) {
        image = new Image(new ByteArrayInputStream(auction.getImageContent()));
      }

      controller.setData(image, auction);
      controller.setAuctionId(auctionId);
      controller.setSocketClient(socket);
      controller.setUserInfo(currentUser, currentUserName);
      controller.setMainPageController(this);
      productContainer.getChildren().add(productBox);
      displayedAuctionIds.add(auctionId);
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
      controller.setCurrentUser(currentUser);
      controller.setSocketClient(socket);

      Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
      stage.getScene().setRoot(root);
      stage.show();
    } catch (IOException e) {
      LOGGER.error("Không thể chuyển sang trang tạo auction", e);
    }
  }

  /**
  * Refresh danh sách auction trên main page.
  */
  @FXML
  private void refreshAuctionList() {
    requestApprovedAuctionList();
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
      ConcurrentHashMap<Integer,Auction> auctionList = response.getAuctionList();
      LOGGER.info("Nhận danh sách auction đã duyệt [size: {}]", auctionList.size());

      productContainer.getChildren().clear();
      displayedAuctionIds.clear();
      if (auctionList.isEmpty()) {
        Label emptyLabel = new Label("Chưa có phiên đấu giá nào được duyệt");
        emptyLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 14;");
        productContainer.getChildren().add(emptyLabel);
        return;
      }
      for (Integer id : auctionList.keySet()) {
        Auction auction = auctionList.get(id);
        if (auction != null) {
          LOGGER.info(
              "Hiển thị auction trên main page [auctionId: {}, name: {}]",
              auction.getId(),
              auction.getItemName());
          addProductBox(auction.getId(), auction);
        }
      }
    } else if (obj instanceof Auction) {
      Auction auction = (Auction) obj;
      addProductBox(auction.getId(), auction);
    } else if (obj instanceof JoinRoomResponse) {
      JoinRoomResponse response = (JoinRoomResponse) obj;
      if (response.getResponse()) {
        switchToAuctionDetail(pendingAuctionId);
      } else {
        LOGGER.warn("Không thể join auction room [auctionId: {}]", pendingAuctionId);
      }
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
          currentStage.setTitle("Đăng nhập");
          currentStage.setWidth(LOGIN_PAGE_WIDTH);
          currentStage.setHeight(LOGIN_PAGE_HEIGHT);
          currentStage.centerOnScreen();
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

  /**
  * Gửi request join room trước khi mở chi tiết auction.
  *
  * @param auctionId id auction cần mở
  */
  public void joinAuctionRoom(int auctionId) {
    this.pendingAuctionId = auctionId;
    socket.sendRequest(new JoinRoomRequest(auctionId));
    LOGGER.info("Đã gửi JoinRoomRequest [auctionId: {}]", auctionId);
  }

  /**
  * Chuyển sang màn chi tiết auction.
  *
  * @param auctionId id auction cần hiển thị
  */
  private void switchToAuctionDetail(int auctionId) {
    try {
      FXMLLoader loader =
          new FXMLLoader(getClass().getResource("/fxml/AuctionDetailView.fxml"));
      Parent root = loader.load();

      AuctionDetailController controller = loader.getController();
      controller.setAuctionData(auctionId, currentUserName, currentUser);
      controller.setSocketClient(socket);

      Stage stage = (Stage) usernameLabel.getScene().getWindow();
      stage.getScene().setRoot(root);
      stage.setTitle("Chi tiết đấu giá");
      stage.setWidth(AUCTION_DETAIL_WIDTH);
      stage.setHeight(AUCTION_DETAIL_HEIGHT);
      stage.centerOnScreen();
      stage.show();
    } catch (IOException e) {
      LOGGER.error("Không thể mở màn chi tiết auction", e);
    }
  }
}
