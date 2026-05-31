package com.auction.client.controller.auction;

import com.auction.client.controller.Controller;
import com.auction.client.network.SocketClient;
import com.auction.shared.enums.CreateAuctionStatus;
import com.auction.shared.model.User;
import com.auction.shared.request.auction.CreateAuctionRequest;
import com.auction.shared.request.auction.GetMyAuctionHistoryRequest;
import com.auction.shared.request.auction.PendingAuctionReviewRequest;
import com.auction.shared.response.auction.GetMyAuctionHistoryResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller hiển thị lịch sử auction đã được duyệt của user hiện tại.
 */
public class MyAuctionHistoryController extends Controller {
  private static final Logger LOGGER = LoggerFactory.getLogger(MyAuctionHistoryController.class);
  private static final DateTimeFormatter END_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

  private SocketClient socket;
  private User currentUser;
  private String currentUserName;

  @FXML
  private Label usernameLabel;

  @FXML
  private VBox historyContainer;

  /**
   * Gán socket client và tải lịch sử auction.
   *
   * @param socket socket client
   */
  public void setSocketClient(SocketClient socket) {
    this.socket = socket;
    socket.setController(this);
    socket.startListening();
    requestHistory();
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
   * Gán tên user hiện tại.
   *
   * @param currentUserName tên user hiện tại
   */
  public void setUserName(String currentUserName) {
    this.currentUserName = currentUserName;
    usernameLabel.setText(currentUserName);
  }

  @FXML
  private void refreshHistory() {
    requestHistory();
  }

  private void requestHistory() {
    if (socket == null || currentUser == null) {
      showEmptyMessage("Không thể tải lịch sử vì thiếu thông tin user");
      return;
    }
    socket.sendRequest(new GetMyAuctionHistoryRequest(currentUser));
    LOGGER.info("Đã gửi GetMyAuctionHistoryRequest");
  }

  @Override
  public void handle(Object obj) {
    if (obj instanceof GetMyAuctionHistoryResponse) {
      GetMyAuctionHistoryResponse response =
          (GetMyAuctionHistoryResponse) obj;
      renderHistory(response.getRequestList());
    }
  }

  private void renderHistory(LinkedHashMap<Integer, PendingAuctionReviewRequest> requestList) {
    historyContainer.getChildren().clear();
    if (requestList == null || requestList.isEmpty()) {
      showEmptyMessage("Chưa có auction nào của bạn được admin xử lý");
      return;
    }

    for (Integer requestId : requestList.keySet()) {
      PendingAuctionReviewRequest request = requestList.get(requestId);
      if (request != null) {
        addHistoryRow(request);
      }
    }
  }

  private void addHistoryRow(PendingAuctionReviewRequest pendingRequest) {
    CreateAuctionRequest request = pendingRequest.getCreateAuctionRequest();
    HBox row = new HBox(16);
    row.setAlignment(Pos.CENTER_LEFT);
    row.setPrefHeight(120);
    row.setStyle(
        "-fx-padding: 12; -fx-background-color: white; -fx-border-color: #e5e7eb; "
            + "-fx-border-radius: 8; -fx-background-radius: 8;");

    ImageView imageView = new ImageView();
    imageView.setFitWidth(120);
    imageView.setFitHeight(90);
    imageView.setPreserveRatio(true);
    if (request.getImageContent() != null && request.getImageContent().length > 0) {
      imageView.setImage(new Image(new ByteArrayInputStream(request.getImageContent())));
    }

    VBox infoBox = new VBox(6);
    HBox.setHgrow(infoBox, javafx.scene.layout.Priority.ALWAYS);

    Label nameLabel = new Label(request.getName());
    nameLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #111827;");

    Label sellerLabel = new Label("Người tạo: " + pendingRequest.getUser().getUsername());
    sellerLabel.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 13;");

    Label categoryLabel = new Label("Danh mục: " + request.getCategory());
    categoryLabel.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 13;");

    Label endTimeLabel = new Label("Kết thúc: " + request.getEndTime().format(END_TIME_FORMATTER));
    endTimeLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 13;");
    infoBox.getChildren().addAll(nameLabel, sellerLabel, categoryLabel, endTimeLabel);

    VBox statusBox = new VBox(6);
    statusBox.setAlignment(Pos.CENTER);
    statusBox.setMinWidth(160);
    Label statusTitleLabel = new Label("Trạng thái");
    statusTitleLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12;");
    Label statusLabel = new Label();
    renderStatus(statusLabel, pendingRequest.getStatus());
    statusBox.getChildren().addAll(statusTitleLabel, statusLabel);

    Region spacer = new Region();
    HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

    VBox priceBox = new VBox(6);
    priceBox.setAlignment(Pos.CENTER_RIGHT);
    Label priceTitleLabel = new Label("Giá khởi điểm");
    priceTitleLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12;");
    Label priceLabel = new Label("$" + String.format("%.2f", request.getStartingPrice()));
    priceLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #111827;");
    priceBox.getChildren().addAll(priceTitleLabel, priceLabel);

    row.getChildren().addAll(imageView, infoBox, statusBox, spacer, priceBox);
    historyContainer.getChildren().add(row);
  }

  private void renderStatus(Label statusLabel, CreateAuctionStatus status) {
    if (status == CreateAuctionStatus.SUCCESS) {
      statusLabel.setText("CHẤP NHẬN");
      statusLabel.setStyle(
          "-fx-padding: 8 16; -fx-background-radius: 999; "
              + "-fx-background-color: #dcfce7; -fx-text-fill: #16a34a; "
              + "-fx-font-weight: bold; -fx-font-size: 14;");
    } else {
      statusLabel.setText("TỪ CHỐI");
      statusLabel.setStyle(
          "-fx-padding: 8 16; -fx-background-radius: 999; "
              + "-fx-background-color: #fef2f2; -fx-text-fill: #dc2626; "
              + "-fx-font-weight: bold; -fx-font-size: 14;");
    }
  }

  private void showEmptyMessage(String message) {
    historyContainer.getChildren().clear();
    Label emptyLabel = new Label(message);
    emptyLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 14;");
    historyContainer.getChildren().add(emptyLabel);
  }

  @FXML
  private void switchToMainPage(ActionEvent event) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainPageView.fxml"));
      Parent root = loader.load();

      MainPageController controller = loader.getController();
      controller.setCurrentUser(currentUser);
      controller.setSocketClient(socket);

      Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
      stage.getScene().setRoot(root);
      stage.setTitle("Hệ thống đấu giá");
      stage.show();
    } catch (IOException e) {
      LOGGER.error("Không thể quay về main page", e);
    }
  }
}
