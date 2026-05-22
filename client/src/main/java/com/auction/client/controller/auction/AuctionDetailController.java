package com.auction.client.controller.auction;

import com.auction.client.controller.Controller;
import com.auction.client.network.SocketClient;
import com.auction.shared.auction.Auction;
import com.auction.shared.auction.BidTransaction;
import com.auction.shared.enums.AuctionStatus;
import com.auction.shared.enums.BidResponseStatus;
import com.auction.shared.enums.BidderStatus;
import com.auction.shared.model.User;
import com.auction.shared.request.auction.BidRequest;
import com.auction.shared.request.auction.GetAuctionDetailRequest;
import com.auction.shared.request.auction.LeaveRoomRequest;
import com.auction.shared.response.auction.BidResultResponse;
import com.auction.shared.response.auction.BidUpdateResponse;
import com.auction.shared.response.auction.GetAuctionDetailResponse;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller cho màn hình chi tiết đấu giá.
 */
public class AuctionDetailController extends Controller implements Initializable {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(AuctionDetailController.class);

  private SocketClient socket;
  private int auctionId;
  private String currentUserName;
  private User currentUser;
  private Auction currentAuction;
  private BidderStatus currentBidderStatus;
  private double quickBid1Value;
  private double quickBid2Value;

  @FXML private Button backButton;
  @FXML private Label auctionTitleLabel;
  @FXML private Label auctionStatusBadge;
  @FXML private Label itemNameLabel;
  @FXML private Label descriptionLabel;
  @FXML private Label currentPriceLabel;
  @FXML private Label bidderStatusLabel;
  @FXML private Label bidderSubStatusLabel;
  @FXML private HBox quickBidBox;
  @FXML private Button quickBid1Button;
  @FXML private Button quickBid2Button;
  @FXML private Label orLabel;
  @FXML private Label minimumBidLabel;
  @FXML private HBox bidInputBox;
  @FXML private TextField bidAmountField;
  @FXML private Button placeBidButton;
  @FXML private Label bidResultLabel;
  @FXML private Label endDateLabel;
  @FXML private Label totalBidsLabel;
  @FXML private Label sellerLabel;
  @FXML private HBox winnerBox;
  @FXML private Label winnerLabel;
  @FXML private ImageView productImageView;
  @FXML private VBox bidHistoryContainer;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    showLoadingState();
  }

  /**
   * Trạng thái chờ response để tránh hiển thị nhầm UI bid trước khi có dữ liệu thật.
   */
  private void showLoadingState() {
    auctionTitleLabel.setText("Đang tải chi tiết đấu giá...");
    auctionStatusBadge.setText("");
    itemNameLabel.setText("");
    descriptionLabel.setText("");
    currentPriceLabel.setText("");
    bidderStatusLabel.setText("");
    bidderSubStatusLabel.setText("");
    minimumBidLabel.setText("");
    bidResultLabel.setText("");
    endDateLabel.setText("");
    totalBidsLabel.setText("");
    sellerLabel.setText("");
    winnerLabel.setText("");
    productImageView.setImage(null);
    bidHistoryContainer.getChildren().clear();

    hideBidControls();
    winnerBox.setVisible(false);
    winnerBox.setManaged(false);
  }

  /**
   * Gán socket client và gửi request lấy chi tiết auction.
   *
   * @param socket socket client
   */
  public void setSocketClient(SocketClient socket) {
    this.socket = socket;
    socket.setController(this);
    socket.startListening();
    // Gửi request lấy chi tiết auction
    socket.sendRequest(new GetAuctionDetailRequest(auctionId));
    LOGGER.info("Đã gửi GetAuctionDetailRequest [auctionId: {}]", auctionId);
  }

  /**
   * Gán thông tin auction cơ bản trước khi load chi tiết.
   *
   * @param auctionId ID phiên đấu giá
   * @param currentUserName tên user hiện tại
   * @param currentUser user hiện tại
   */
  public void setAuctionData(int auctionId, String currentUserName, User currentUser) {
    this.auctionId = auctionId;
    this.currentUserName = currentUserName;
    this.currentUser = currentUser;
  }

  /**
   * Gán tên user hiện tại.
   *
   * @param currentUserName tên user
   */
  public void setUserName(String currentUserName) {
    this.currentUserName = currentUserName;
  }

  /**
   * Gán user hiện tại.
   *
   * @param currentUser user hiện tại
   */
  public void setCurrentUser(User currentUser) {
    this.currentUser = currentUser;
  }

  /**
   * Render giao diện dựa trên response chi tiết auction.
   *
   * @param response response từ server
   */
  private void renderAuctionDetail(GetAuctionDetailResponse response) {
    Auction auction = response.getAuction();
    AuctionStatus auctionStatus = response.getAuctionStatus();
    BidderStatus bidderStatus = response.getBidderStatus();
    this.currentAuction = auction;
    this.currentBidderStatus = bidderStatus;

    if (auction == null) {
      LOGGER.error("Auction null trong response [auctionId: {}]", auctionId);
      return;
    }

    // Thông tin chung
    auctionTitleLabel.setText("Chi tiết đấu giá #" + auction.getId());
    itemNameLabel.setText(auction.getItemName());
    descriptionLabel.setText(auction.getDescription());
    currentPriceLabel.setText("Bid hiện tại: $"
        + String.format("%.2f", auction.getCurrentPrice()));
    sellerLabel.setText(auction.getSeller().getUsername());
    endDateLabel.setText("Kết thúc: " + auction.getEndTime().toString());
    totalBidsLabel.setText("Tổng số bid: " + auction.getBidHistory().size());

    double minBid = auction.getCurrentPrice() + auction.getMinimumIncrement();
    minimumBidLabel.setText("Bid tối thiểu: $" + String.format("%.2f", minBid));

    // Ảnh sản phẩm
    if (auction.getImageContent() != null && auction.getImageContent().length > 0) {
      Image image = new Image(new ByteArrayInputStream(auction.getImageContent()));
      productImageView.setImage(image);
    }

    // Lịch sử khi mới vào phòng: render lại toàn bộ từ mảng ban đầu
    initBidHistoryView(auction.getBidHistory());

    // Reset trạng thái
    bidResultLabel.setText("");
    hideBidControls();

    // Xử lý theo trạng thái
    if (auctionStatus == AuctionStatus.CLOSED) {
      renderClosedAuction(auction);
    } else {
      renderOpenAuction(auction, bidderStatus);
    }
  }

  /**
   * Render khi auction đã kết thúc.
   */
  private void renderClosedAuction(Auction auction) {
    auctionStatusBadge.setText("ĐÃ KẾT THÚC");
    auctionStatusBadge.setStyle(
        "-fx-padding: 4 12; -fx-background-radius: 12; "
            + "-fx-background-color: #fecaca; -fx-text-fill: #dc2626; "
            + "-fx-font-weight: bold; -fx-font-size: 12;");

    currentPriceLabel.setStyle("-fx-text-fill: #6b7280;");

    bidderStatusLabel.setText("Phiên đấu giá đã kết thúc");
    bidderStatusLabel.setStyle(
        "-fx-text-fill: #dc2626; -fx-font-weight: bold; -fx-font-size: 14;");
    bidderSubStatusLabel.setText("Chỉ có thể xem lịch sử đặt bid");

    // Hiện winner
    if (auction.getWinner() != null) {
      winnerBox.setVisible(true);
      winnerBox.setManaged(true);
      winnerLabel.setText(auction.getWinner().getUsername());
    }

    // Ẩn tất cả controls bid
    hideBidControls();
  }

  /**
   * Render khi auction đang mở, dựa theo BidderStatus.
   */
  private void renderOpenAuction(Auction auction, BidderStatus bidderStatus) {
    auctionStatusBadge.setText("ĐANG MỞ");
    auctionStatusBadge.setStyle(
        "-fx-padding: 4 12; -fx-background-radius: 12; "
            + "-fx-background-color: #dcfce7; -fx-text-fill: #16a34a; "
            + "-fx-font-weight: bold; -fx-font-size: 12;");

    currentPriceLabel.setStyle(
        "-fx-text-fill: #16a34a; -fx-font-weight: bold; -fx-font-size: 28;");

    switch (bidderStatus) {
      case VIEWER_ONLY:
        renderSellerView();
        break;
      case CURRENT_WINNER:
        renderCurrentWinnerView(auction);
        break;
      case OUTBID:
        renderOutbidView(auction);
        break;
      case VIEWER:
        renderViewerView(auction);
        break;
      default:
        renderViewerView(auction);
        break;
    }
  }

  /**
   * Seller view - không có nút bid.
   */
  private void renderSellerView() {
    bidderStatusLabel.setText("Bạn là người bán sản phẩm này");
    bidderStatusLabel.setStyle(
        "-fx-text-fill: #6b7280; -fx-font-weight: bold; -fx-font-size: 14;");
    bidderSubStatusLabel.setText("Bạn không thể đặt bid cho sản phẩm của mình");
    hideBidControls();
  }

  /**
   * Đang thắng - có nút bid để tiếp tục đặt cao hơn.
   */
  private void renderCurrentWinnerView(Auction auction) {
    bidderStatusLabel.setText("✅ Bạn là người đặt bid cao nhất ở hiện tại");
    bidderStatusLabel.setStyle(
        "-fx-text-fill: #16a34a; -fx-font-weight: bold; -fx-font-size: 14;");
    bidderSubStatusLabel.setText(
        "(bạn có thể đặt cao hơn để tránh outbid)");
    showBidInput();
  }

  /**
   * Bị outbid - có quick bid buttons + input thường.
   */
  private void renderOutbidView(Auction auction) {
    bidderStatusLabel.setText("⚠ Bạn đã bị outbid");
    bidderStatusLabel.setStyle(
        "-fx-text-fill: #dc2626; -fx-font-weight: bold; -fx-font-size: 14;");
    bidderSubStatusLabel.setText(
        "Bạn vẫn có thể thắng! Hãy đặt bid cao hơn");

    // Quick bid buttons
    double minBid = auction.getCurrentPrice() + auction.getMinimumIncrement();
    quickBid1Value = minBid;
    quickBid2Value = minBid + auction.getMinimumIncrement();
    quickBid1Button.setText("Bid $" + String.format("%.0f", quickBid1Value));
    quickBid2Button.setText("Bid $" + String.format("%.0f", quickBid2Value));

    quickBidBox.setVisible(true);
    quickBidBox.setManaged(true);
    orLabel.setVisible(true);
    orLabel.setManaged(true);
    showBidInput();
  }

  /**
   * Viewer thường - chưa bid lần nào, có nút đặt bid.
   */
  private void renderViewerView(Auction auction) {
    bidderStatusLabel.setText("Hãy đặt bid để tham gia đấu giá!");
    bidderStatusLabel.setStyle(
        "-fx-text-fill: #111827; -fx-font-weight: bold; -fx-font-size: 14;");
    bidderSubStatusLabel.setText("");
    showBidInput();
  }

  /**
   * Hiện ô nhập bid.
   */
  private void showBidInput() {
    bidInputBox.setVisible(true);
    bidInputBox.setManaged(true);
    minimumBidLabel.setVisible(true);
    minimumBidLabel.setManaged(true);
  }

  /**
   * Ẩn tất cả controls bid.
   */
  private void hideBidControls() {
    bidInputBox.setVisible(false);
    bidInputBox.setManaged(false);
    quickBidBox.setVisible(false);
    quickBidBox.setManaged(false);
    orLabel.setVisible(false);
    orLabel.setManaged(false);
  }

  /**
   * Hàm trợ giúp tạo UI cho một dòng lịch sử đấu giá độc lập.
   */
  private HBox createBidRow(BidTransaction bid, boolean isLatest) {
    HBox bidRow = new HBox(10);
    bidRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

    Label userLabel = new Label(bid.getBidderUsername());
    userLabel.setStyle("-fx-text-fill: #065f46; -fx-font-weight: bold;");

    Label separator = new Label(" | ");
    separator.setStyle("-fx-text-fill: #6ee7b7;");

    Label amountLabel = new Label("$" + String.format("%.2f", bid.getBidAmount()));
    amountLabel.setStyle("-fx-text-fill: #065f46; -fx-font-weight: bold;");

    bidRow.getChildren().addAll(userLabel, separator, amountLabel);

    if (isLatest) {
      bidRow.setStyle(
          "-fx-background-color: #bbf7d0; -fx-padding: 8 12; "
              + "-fx-background-radius: 6; -fx-border-color: #4ade80; "
              + "-fx-border-radius: 6;");
    } else {
      bidRow.setStyle(
          "-fx-background-color: #d1fae5; -fx-padding: 8 12; "
              + "-fx-background-radius: 6; -fx-border-color: #a7f3d0; "
              + "-fx-border-radius: 6;");
    }
    return bidRow;
  }

  /**
   * Khởi tạo toàn bộ danh sách lịch sử khi người dùng mới vào phòng.
   */
  private void initBidHistoryView(ArrayList<BidTransaction> bidHistory) {
    bidHistoryContainer.getChildren().clear();
    if (bidHistory == null || bidHistory.isEmpty()) {
      Label emptyLabel = new Label("Chưa có bid nào");
      emptyLabel.setStyle("-fx-text-fill: #9ca3af; -fx-font-style: italic;");
      bidHistoryContainer.getChildren().add(emptyLabel);
      return;
    }

    for (int i = bidHistory.size() - 1; i >= 0; i--) {
      BidTransaction bid = bidHistory.get(i);
      boolean isLatest = (i == bidHistory.size() - 1);
      HBox bidRow = createBidRow(bid, isLatest);
      bidHistoryContainer.getChildren().add(bidRow);
    }
  }

  /**
   * Render danh sách lịch sử bid.
   *
   * @param bidHistory danh sách bid
   */
  private void renderBidHistory(ArrayList<BidTransaction> bidHistory) {
    if (bidHistory == null || bidHistory.isEmpty()) {
      return;
    }

    BidTransaction latestBid = bidHistory.get(bidHistory.size() - 1);
    HBox newBidRow = createBidRow(latestBid, true);
    if (bidHistoryContainer.getChildren().size() > 0 && !(bidHistoryContainer.getChildren().get(0) instanceof Label)) {
      Node previousTopRow = bidHistoryContainer.getChildren().get(0);
      previousTopRow.setStyle(
          "-fx-background-color: #d1fae5; -fx-padding: 8 12; "
          + "-fx-background-radius: 6; -fx-border-color: #a7f3d0; "
          + "-fx-border-radius: 6;"
      );
    }


    if (!bidHistoryContainer.getChildren().isEmpty() && bidHistoryContainer.getChildren().get(0) instanceof Label) {
      bidHistoryContainer.getChildren().clear();
    }

    bidHistoryContainer.getChildren().add(0, newBidRow);
  }

  /**
   * Xử lý đặt bid từ ô nhập.
   */
  @FXML
  private void handlePlaceBid(ActionEvent event) {
    String bidText = bidAmountField.getText().trim();
    if (bidText.isEmpty()) {
      bidResultLabel.setText("Vui lòng nhập số tiền bid");
      bidResultLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 13;");
      return;
    }

    try {
      double bidPrice = Double.parseDouble(bidText);
      sendBidRequest(bidPrice);
    } catch (NumberFormatException e) {
      bidResultLabel.setText("Số tiền không hợp lệ");
      bidResultLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 13;");
    }
  }

  /**
   * Quick bid button 1.
   */
  @FXML
  private void handleQuickBid1(ActionEvent event) {
    sendBidRequest(quickBid1Value);
  }

  /**
   * Quick bid button 2.
   */
  @FXML
  private void handleQuickBid2(ActionEvent event) {
    sendBidRequest(quickBid2Value);
  }

  /**
   * Gửi BidRequest lên server.
   *
   * @param bidPrice giá bid
   */
  private void sendBidRequest(double bidPrice) {
    if (currentUser == null) {
      bidResultLabel.setText("Không xác định được người dùng");
      bidResultLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 13;");
      return;
    }

    BidRequest request = new BidRequest(auctionId, bidPrice, currentUser);
    socket.sendRequest(request);
    LOGGER.info(
        "Đã gửi BidRequest [auctionId: {}, bidPrice: {}, bidder: {}]",
        auctionId, bidPrice, currentUser.getEmail());
    bidResultLabel.setText("Đang xử lý bid...");
    bidResultLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 13;");
  }

  /**
   * Quay về main page và gửi LeaveRoomRequest.
   */
  @FXML
  private void handleBack(ActionEvent event) {
    // Gửi leave room
    socket.sendRequest(new LeaveRoomRequest(auctionId));
    LOGGER.info("Đã gửi LeaveRoomRequest [auctionId: {}]", auctionId);

    try {
      FXMLLoader loader =
          new FXMLLoader(getClass().getResource("/fxml/MainPageView.fxml"));
      Parent root = loader.load();

      MainPageController controller = loader.getController();
      controller.setUserName(currentUserName);
      controller.setCurrentUser(currentUser);
      controller.setSocketClient(socket);

      Stage stage = (Stage) backButton.getScene().getWindow();
      stage.getScene().setRoot(root);
      stage.setTitle("Hệ thống đấu giá");
      stage.show();
    } catch (IOException e) {
      LOGGER.error("Không thể quay về trang chính", e);
    }
  }

  @Override
  public void handle(Object obj) {
    Platform.runLater(() -> {
      if (obj instanceof GetAuctionDetailResponse) {
        GetAuctionDetailResponse response = (GetAuctionDetailResponse) obj;
        LOGGER.info(
            "Nhận GetAuctionDetailResponse [auctionId: {}, status: {}, bidderStatus: {}]",
            auctionId,
            response.getAuctionStatus(),
            response.getBidderStatus());
        renderAuctionDetail(response);

      } else if (obj instanceof BidResultResponse) {
        BidResultResponse response = (BidResultResponse) obj;
        LOGGER.info(
            "Nhận BidResultResponse [status: {}, message: {}]",
            response.getBidResponseStatus(),
            response.getMessage());

        if (response.getBidResponseStatus() == BidResponseStatus.ACCEPTED) {
          bidResultLabel.setText("✅ " + response.getMessage());
          bidResultLabel.setStyle(
              "-fx-text-fill: #16a34a; -fx-font-weight: bold; -fx-font-size: 13;");
          bidAmountField.clear();
        } else {
          bidResultLabel.setText("❌ " + response.getMessage());
          bidResultLabel.setStyle(
              "-fx-text-fill: #dc2626; -fx-font-weight: bold; -fx-font-size: 13;");
        }

      } else if (obj instanceof BidUpdateResponse) {
        BidUpdateResponse update = (BidUpdateResponse) obj;
        LOGGER.info(
            "Nhận BidUpdateResponse [auctionId: {}, currentPrice: {}, winner: {}]",
            update.getAuctionId(),
            update.getCurrentPrice(),
            update.getCurrentWinnerUsername());

        handleBidUpdate(update);
      }
    });
  }

  /**
   * Cập nhật realtime khi nhận gói thầu broadcast về từ socket.
   *
   * @param update thông tin nâng giá mới
   */
  private void handleBidUpdate(BidUpdateResponse update) {
    // 1. Cập nhật các thông tin giá tiền cơ bản
    currentPriceLabel.setText("Bid hiện tại: $"
        + String.format("%.2f", update.getCurrentPrice()));

    if (currentAuction != null) {
      currentAuction.setCurrentPrice(update.getCurrentPrice());
  
    }

    double minIncrement = (currentAuction != null) ? currentAuction.getMinimumIncrement() : 0;
    double minBid = update.getCurrentPrice() + minIncrement;
    minimumBidLabel.setText("Bid tối thiểu: $" + String.format("%.2f", minBid));

    // 2. GỌI LOGIC TỐI ƯU: Chỉ bóc phần tử cuối cùng của mảng truyền vào đẩy lên UI
    renderBidHistory(update.getBidHistory());
    totalBidsLabel.setText("Tổng số bid: " + update.getBidHistory().size());

    // 3. Cập nhật trạng thái người đấu giá
    if (currentUser != null
        && currentUser.getEmail().equals(update.getCurrentWinnerEmail())) {
      currentBidderStatus = BidderStatus.CURRENT_WINNER;
      bidderStatusLabel.setText("✅ Bạn là người đặt bid cao nhất ở hiện tại");
      bidderStatusLabel.setStyle(
          "-fx-text-fill: #16a34a; -fx-font-weight: bold; -fx-font-size: 14;");
      bidderSubStatusLabel.setText(
          "(bạn có thể đặt cao hơn để tránh outbid)");
      quickBidBox.setVisible(false);
      quickBidBox.setManaged(false);
      orLabel.setVisible(false);
      orLabel.setManaged(false);
      showBidInput();
    } else if (currentBidderStatus == BidderStatus.CURRENT_WINNER
        || currentBidderStatus == BidderStatus.OUTBID) {
      currentBidderStatus = BidderStatus.OUTBID;
      bidderStatusLabel.setText("⚠ Bạn đã bị outbid");
      bidderStatusLabel.setStyle(
          "-fx-text-fill: #dc2626; -fx-font-weight: bold; -fx-font-size: 14;");
      bidderSubStatusLabel.setText(
          "Bạn vẫn có thể thắng! Hãy đặt bid cao hơn");

      quickBid1Value = minBid;
      quickBid2Value = minBid + minIncrement;
      quickBid1Button.setText("Bid $" + String.format("%.0f", quickBid1Value));
      quickBid2Button.setText("Bid $" + String.format("%.0f", quickBid2Value));
      quickBidBox.setVisible(true);
      quickBidBox.setManaged(true);
      orLabel.setVisible(true);
      orLabel.setManaged(true);
      showBidInput();
    }
  }
}
