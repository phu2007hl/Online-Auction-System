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
import com.auction.shared.response.auction.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
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
  private static final DateTimeFormatter BID_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("HH:mm:ss");
  private static final int MAX_CHART_POINTS = 8;
  private static final double CHART_POINT_HOVER_THRESHOLD = 60;
  private static final double CHART_LINE_HOVER_THRESHOLD = 42;
  private static final double MAIN_PAGE_WIDTH = 1100;
  private static final double MAIN_PAGE_HEIGHT = 720;

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
  @FXML private Label chartLatestPriceLabel;
  @FXML private Label chartPointCountLabel;
  @FXML private AnchorPane chartPlotPane;
  @FXML private VBox chartInfoPopup;
  @FXML private Label chartInfoUserLabel;
  @FXML private Label chartInfoBidLabel;
  @FXML private Label chartInfoTimeLabel;
  @FXML private LineChart<String, Number> priceChart;
  @FXML private CategoryAxis timeAxis;
  @FXML private NumberAxis priceAxis;
  private XYChart.Series<String, Number> chartSeries;
  private XYChart.Data<String, Number> activeHoverPoint;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    configureBidChart();
    showLoadingState();
  }

  /**
   * Cấu hình biểu đồ giá realtime.
   */
  private void configureBidChart() {
    if (priceChart == null) {
      return;
    }
    priceChart.setAnimated(false);
    priceChart.setCreateSymbols(true);
    priceChart.setLegendVisible(false);
    priceChart.setHorizontalGridLinesVisible(true);
    priceChart.setVerticalGridLinesVisible(true);
    priceChart.setStyle(
        "-fx-background-color: white; "
            + "-fx-font-size: 12; "
            + "-fx-text-fill: #374151;");
    priceChart.addEventFilter(MouseEvent.MOUSE_MOVED, this::showNearestBidTooltip);
    if (chartPlotPane != null) {
      chartPlotPane.setPickOnBounds(true);
      chartPlotPane.addEventFilter(MouseEvent.MOUSE_MOVED, this::showNearestBidTooltip);
      chartPlotPane.setOnMouseExited(this::hideChartHoverTooltipIfOutsidePlot);
    }

    timeAxis.setLabel("Thời gian");
    timeAxis.setTickLabelFill(javafx.scene.paint.Color.web("#374151"));
    timeAxis.setTickLabelRotation(0);
    timeAxis.setStyle(
        "-fx-tick-label-fill: #374151; "
            + "-fx-font-size: 11;");

    priceAxis.setLabel("Giá ($)");
    priceAxis.setForceZeroInRange(false);
    priceAxis.setAutoRanging(true);
    priceAxis.setTickLabelFill(javafx.scene.paint.Color.web("#374151"));
    priceAxis.setTickLabelFormatter(
        new NumberAxis.DefaultFormatter(priceAxis) {
          @Override
          public String toString(Number value) {
            double amount = value.doubleValue();
            if (Math.abs(amount) >= 1000000) {
              return String.format("%.1fM", amount / 1000000);
            }
            if (Math.abs(amount) >= 1000) {
              return String.format("%.0fK", amount / 1000);
            }
            return String.format("%.0f", amount);
          }
        });
    priceAxis.setStyle(
        "-fx-tick-label-fill: #374151; "
            + "-fx-font-size: 11;");
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
    clearBidChart();

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
 * Cập nhật giao diện khi auction được chỉnh sửa.
 *
 * @param update dữ liệu auction mới
 */
private void updateAuction(UpdateAuctionResponse update) {

  // Update item name
  itemNameLabel.setText(update.getUpdatedAuction().getItemName());

  // Update description
  descriptionLabel.setText(update.getUpdatedAuction().getDescription());

  // Update category if bạn có label category
  // categoryLabel.setText(update.getCategory());

  // Update image
  if (update.getUpdatedAuction().getImageContent() != null
      && update.getUpdatedAuction().getImageContent().length > 0) {

    Image image =
        new Image(new ByteArrayInputStream(update.getUpdatedAuction().getImageContent()));

    productImageView.setImage(image);
  }
  if (AuctionStatus.CANCELLED.equals(update.getUpdatedAuction().getAuctionStatus())){
    if (currentAuction != null) {
      currentAuction.setStatus(AuctionStatus.CANCELLED);
    }
    auctionStatusBadge.setText("ĐÃ BỊ HUỶ");
    auctionStatusBadge.setStyle(
        "-fx-padding: 4 12; -fx-background-radius: 12; "
            + "-fx-background-color: #868383; -fx-text-fill: #c5bcbc; "
            + "-fx-font-weight: bold; -fx-font-size: 12;");
            hideBidControls();
  }

  // Update local auction object
  if (currentAuction != null) {
    currentAuction.setItemName(update.getUpdatedAuction().getItemName());
    currentAuction.setDescription(update.getUpdatedAuction().getDescription());
    currentAuction.setCategory(update.getUpdatedAuction().getCategory());
    currentAuction.setImageContent(update.getUpdatedAuction().getImageContent());
    currentAuction.setStatus(update.getUpdatedAuction().getAuctionStatus());
    currentAuction.setImageContent(update.getUpdatedAuction().getImageContent());


    // Nếu Auction có category
    // currentAuction.setCategory(update.getCategory());

  }

  LOGGER.info(
      "Phiên đấu giá đã được cập nhật [auctionId: {}]",
      update.getUpdatedAuction().getId());
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
    renderBidChart(auction.getBidHistory());

    // Reset trạng thái
    bidResultLabel.setText("");
    hideBidControls();

    // Xử lý theo trạng thái
    if (auctionStatus == AuctionStatus.CLOSED) {
      renderClosedAuction(auction);
    } else if (auctionStatus == AuctionStatus.CANCELLED) {
      renderCancelledAuction(auction);
    }
    else{
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
  private void renderCancelledAuction(Auction auction){
      auctionStatusBadge.setText("ĐÃ BỊ HUỶ");
    auctionStatusBadge.setStyle(
        "-fx-padding: 4 12; -fx-background-radius: 12; "
            + "-fx-background-color: #868383; -fx-text-fill: #c5bcbc; "
            + "-fx-font-weight: bold; -fx-font-size: 12;");

    currentPriceLabel.setStyle("-fx-text-fill: #6b7280;");

    bidderStatusLabel.setText("Phiên đấu giá đã bị huỷ");
    bidderStatusLabel.setStyle(
        "-fx-text-fill: #c71e1e; -fx-font-weight: bold; -fx-font-size: 14;");
    bidderSubStatusLabel.setText("Chỉ có thể xem lịch sử đặt bid");


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
   * Xóa dữ liệu biểu đồ giá.
   */
  private void clearBidChart() {
    if (priceChart != null) {
      priceChart.getData().clear();
    }
    if (chartLatestPriceLabel != null) {
      chartLatestPriceLabel.setText("");
    }
    if (chartPointCountLabel != null) {
      chartPointCountLabel.setText("");
    }
    chartSeries = null;
    hideChartHoverTooltip();
  }

  /**
   * Render biểu đồ giá theo thời gian từ bid history.
   * Chỉ hiển thị tối đa {@code MAX_CHART_POINTS} bid gần nhất.
   *
   * @param bidHistory danh sách bid
   */
  private void renderBidChart(ArrayList<BidTransaction> bidHistory) {
    if (priceChart == null) {
      return;
    }
    priceChart.getData().clear();
    chartSeries = null;

    if (bidHistory == null || bidHistory.isEmpty()) {
      return;
    }

    chartSeries = new XYChart.Series<>();
    chartSeries.setName("Giá đấu");

    int start = Math.max(0, bidHistory.size() - MAX_CHART_POINTS);
    for (int i = start; i < bidHistory.size(); i++) {
      BidTransaction bid = bidHistory.get(i);
      LocalDateTime bidTime = bid.getBidTime();
      String timeLabel;
      if (bidTime == null) {
        timeLabel = "Bid " + (i + 1);
      } else {
        timeLabel = bidTime.format(BID_TIME_FORMATTER);
      }
      chartSeries.getData().add(createBidChartPoint(timeLabel, bid, false));
    }

    priceChart.getData().add(chartSeries);
    updateChartSummary(bidHistory, chartSeries.getData().size());
    updatePriceAxisRange();
    Platform.runLater(this::styleBidChart);
  }

  /**
   * Thêm bid mới nhất vào biểu đồ mà không vẽ lại toàn bộ.
   *
   * @param bidHistory danh sách bid đầy đủ
   */
  private void addLatestBidToChart(ArrayList<BidTransaction> bidHistory) {
    if (priceChart == null || bidHistory == null || bidHistory.isEmpty()) {
      return;
    }

    if (chartSeries == null) {
      renderBidChart(bidHistory);
      return;
    }

    BidTransaction latestBid = bidHistory.get(bidHistory.size() - 1);
    LocalDateTime bidTime = latestBid.getBidTime();
    String timeLabel;
    if (bidTime == null) {
      timeLabel = "Bid " + bidHistory.size();
    } else {
      timeLabel = bidTime.format(BID_TIME_FORMATTER);
    }
    chartSeries.getData().add(createBidChartPoint(timeLabel, latestBid, true));

    if (chartSeries.getData().size() > MAX_CHART_POINTS) {
      chartSeries.getData().remove(0);
    }

    updateChartSummary(bidHistory, chartSeries.getData().size());
    updatePriceAxisRange();
    Platform.runLater(this::styleBidChart);
  }

  /**
   * Tạo một điểm trên chart và gắn tooltip cho điểm đó.
   *
   * @param timeLabel nhãn thời gian trên trục X
   * @param bid dữ liệu bid
   * @param latest true nếu là bid mới nhất
   * @return data point của line chart
   */
  private XYChart.Data<String, Number> createBidChartPoint(
      String timeLabel, BidTransaction bid, boolean latest) {
    XYChart.Data<String, Number> point =
        new XYChart.Data<>(timeLabel, bid.getBidAmount());
    point.setExtraValue(bid);
    return point;
  }

  /**
   * Style node symbol sau khi JavaFX render xong data point.
   *
   * @param point data point trên chart
   * @param latest true nếu là bid mới nhất
   */
  private void styleBidPoint(XYChart.Data<String, Number> point, boolean latest) {
    Node node = point.getNode();
    if (node == null) {
      return;
    }
    node.setStyle(createChartSymbolStyle(latest));
  }

  /**
   * Cập nhật thông tin tóm tắt ngay trên biểu đồ.
   *
   * @param bidHistory danh sách bid đầy đủ
   * @param displayedPointCount số điểm đang hiển thị trên chart
   */
  private void updateChartSummary(
      ArrayList<BidTransaction> bidHistory, int displayedPointCount) {
    if (bidHistory == null || bidHistory.isEmpty()) {
      return;
    }
    BidTransaction latestBid = bidHistory.get(bidHistory.size() - 1);
    if (chartLatestPriceLabel != null) {
      chartLatestPriceLabel.setText(
          "Giá mới nhất: $" + String.format("%.2f", latestBid.getBidAmount()));
    }
    if (chartPointCountLabel != null) {
      chartPointCountLabel.setText(
          "Đang hiển thị " + displayedPointCount
              + " bid gần nhất · rê chuột trên biểu đồ để xem chi tiết");
    }
  }

  /**
   * Hiển thị tooltip của điểm bid gần nhất theo trục X khi rê chuột trong vùng chart.
   *
   * @param event sự kiện rê chuột trong biểu đồ
   */
  private void showNearestBidTooltip(MouseEvent event) {
    if (chartSeries == null || chartSeries.getData().isEmpty()) {
      hideChartHoverTooltip();
      return;
    }

    Node plotBackground = priceChart.lookup(".chart-plot-background");
    if (plotBackground == null) {
      hideChartHoverTooltip();
      return;
    }

    Bounds plotBounds = plotBackground.localToScene(plotBackground.getBoundsInLocal());
    if (!plotBounds.contains(event.getSceneX(), event.getSceneY())) {
      hideChartHoverTooltip();
      return;
    }

    XYChart.Data<String, Number> nearestPoint = findNearestChartPoint(event);
    if (nearestPoint == null) {
      hideChartHoverTooltip();
      return;
    }

    Object extraValue = nearestPoint.getExtraValue();
    if (!(extraValue instanceof BidTransaction bid)) {
      hideChartHoverTooltip();
      return;
    }

    LocalDateTime bidTime = bid.getBidTime();
    String timeText;
    if (bidTime == null) {
      timeText = nearestPoint.getXValue();
    } else {
      timeText = bidTime.format(BID_TIME_FORMATTER);
    }
    if (activeHoverPoint != nearestPoint) {
      updateChartInfoPopupText(bid, timeText);
      activeHoverPoint = nearestPoint;
    }

    Node pointNode = nearestPoint.getNode();
    if (pointNode == null) {
      hideChartHoverTooltip();
      return;
    }
    showChartInfoPopup(pointNode);
  }

  /**
   * Cập nhật nội dung bảng thông tin bid trên chart.
   *
   * @param bid dữ liệu bid
   * @param timeText thời gian hiển thị
   */
  private void updateChartInfoPopupText(BidTransaction bid, String timeText) {
    chartInfoUserLabel.setText("User: " + bid.getBidderUsername());
    chartInfoBidLabel.setText("Bid: $" + String.format("%.2f", bid.getBidAmount()));
    chartInfoTimeLabel.setText("Thời gian: " + timeText);
  }

  /**
   * Hiển thị bảng thông tin gần điểm bid trên line. Bảng này mouseTransparent nên
   * không chặn sự kiện rê chuột của chart.
   *
   * @param pointNode node của điểm bid trên line chart
   */
  private void showChartInfoPopup(Node pointNode) {
    if (chartPlotPane == null || chartInfoPopup == null) {
      return;
    }

    Bounds pointBounds = pointNode.localToScene(pointNode.getBoundsInLocal());
    double pointCenterX = pointBounds.getMinX() + pointBounds.getWidth() / 2;
    double pointTopY = pointBounds.getMinY();
    Point2D localPoint = chartPlotPane.sceneToLocal(pointCenterX, pointTopY);

    double popupWidth = chartInfoPopup.getPrefWidth();
    double popupHeight = 78;
    chartInfoPopup.resize(popupWidth, popupHeight);
    double x = localPoint.getX() + 16;
    double y = localPoint.getY() - popupHeight - 10;

    if (x + popupWidth > chartPlotPane.getWidth() - 8) {
      x = localPoint.getX() - popupWidth - 16;
    }
    if (x < 8) {
      x = 8;
    }
    if (y < 8) {
      y = localPoint.getY() + 18;
    }
    if (y + popupHeight > chartPlotPane.getHeight() - 8) {
      y = chartPlotPane.getHeight() - popupHeight - 8;
    }

    chartInfoPopup.setLayoutX(x);
    chartInfoPopup.setLayoutY(y);
    chartInfoPopup.setVisible(true);
    chartInfoPopup.toFront();
  }

  /**
   * Tìm điểm bid gần chuột nhất nếu chuột đang nằm gần điểm hoặc gần đường line.
   *
   * @param event sự kiện rê chuột trong chart
   * @return điểm gần nhất nếu nằm trong ngưỡng hover
   */
  private XYChart.Data<String, Number> findNearestChartPoint(MouseEvent event) {
    ArrayList<XYChart.Data<String, Number>> points = new ArrayList<>();
    ArrayList<double[]> centers = new ArrayList<>();

    for (XYChart.Data<String, Number> point : chartSeries.getData()) {
      Node pointNode = point.getNode();
      if (pointNode == null) {
        continue;
      }
      Bounds pointBounds = pointNode.localToScene(pointNode.getBoundsInLocal());
      double pointCenterX = pointBounds.getMinX() + pointBounds.getWidth() / 2;
      double pointCenterY = pointBounds.getMinY() + pointBounds.getHeight() / 2;
      points.add(point);
      centers.add(new double[] {pointCenterX, pointCenterY});
    }

    XYChart.Data<String, Number> nearestPoint = findNearestRenderedPoint(
        event.getSceneX(), event.getSceneY(), points, centers);
    if (nearestPoint != null) {
      return nearestPoint;
    }

    return findNearestPointOnLine(event.getSceneX(), event.getSceneY(), points, centers);
  }

  /**
   * Tìm điểm bid gần nhất nếu chuột đang gần trực tiếp một node point.
   *
   * @param mouseX tọa độ X của chuột theo scene
   * @param mouseY tọa độ Y của chuột theo scene
   * @param points danh sách data point
   * @param centers tọa độ center của node point
   * @return data point gần nhất hoặc null
   */
  private XYChart.Data<String, Number> findNearestRenderedPoint(
      double mouseX,
      double mouseY,
      ArrayList<XYChart.Data<String, Number>> points,
      ArrayList<double[]> centers) {
    XYChart.Data<String, Number> nearestPoint = null;
    double nearestDistance = Double.MAX_VALUE;

    for (int i = 0; i < centers.size(); i++) {
      double[] center = centers.get(i);
      double distance = distance(mouseX, mouseY, center[0], center[1]);
      if (distance < nearestDistance) {
        nearestDistance = distance;
        nearestPoint = points.get(i);
      }
    }

    if (nearestDistance <= CHART_POINT_HOVER_THRESHOLD) {
      return nearestPoint;
    }
    return null;
  }

  /**
   * Tìm điểm bid đại diện nếu chuột đang gần một đoạn line nối giữa hai bid.
   *
   * @param mouseX tọa độ X của chuột theo scene
   * @param mouseY tọa độ Y của chuột theo scene
   * @param points danh sách data point
   * @param centers tọa độ center của node point
   * @return data point đại diện gần nhất hoặc null
   */
  private XYChart.Data<String, Number> findNearestPointOnLine(
      double mouseX,
      double mouseY,
      ArrayList<XYChart.Data<String, Number>> points,
      ArrayList<double[]> centers) {
    if (centers.size() < 2) {
      return null;
    }

    XYChart.Data<String, Number> nearestPoint = null;
    double nearestDistance = Double.MAX_VALUE;

    for (int i = 0; i < centers.size() - 1; i++) {
      double[] start = centers.get(i);
      double[] end = centers.get(i + 1);
      double t = projectionFactor(mouseX, mouseY, start[0], start[1], end[0], end[1]);
      if (t < 0 || t > 1) {
        continue;
      }
      double projectedX = start[0] + t * (end[0] - start[0]);
      double projectedY = start[1] + t * (end[1] - start[1]);
      double distance = distance(mouseX, mouseY, projectedX, projectedY);
      if (distance < nearestDistance) {
        nearestDistance = distance;
        if (t < 0.5) {
          nearestPoint = points.get(i);
        } else {
          nearestPoint = points.get(i + 1);
        }
      }
    }

    if (nearestDistance <= CHART_LINE_HOVER_THRESHOLD) {
      return nearestPoint;
    }
    return null;
  }

  /**
   * Tính vị trí chiếu của điểm chuột lên đoạn AB. Giá trị 0 là A, 1 là B.
   */
  private double projectionFactor(
      double px, double py, double ax, double ay, double bx, double by) {
    double dx = bx - ax;
    double dy = by - ay;
    double lengthSquared = dx * dx + dy * dy;
    if (lengthSquared == 0) {
      return -1;
    }
    return ((px - ax) * dx + (py - ay) * dy) / lengthSquared;
  }

  /**
   * Tính khoảng cách giữa hai điểm.
   */
  private double distance(double ax, double ay, double bx, double by) {
    double dx = ax - bx;
    double dy = ay - by;
    return Math.sqrt(dx * dx + dy * dy);
  }

  /**
   * Chỉ ẩn tooltip khi chuột thực sự ra khỏi vùng plot. Nếu tooltip popup vừa xuất hiện
   * và gây mouseExited giả trên chart thì vẫn giữ tooltip.
   *
   * @param event sự kiện chuột rời chart
   */
  private void hideChartHoverTooltipIfOutsidePlot(MouseEvent event) {
    Node plotBackground = priceChart.lookup(".chart-plot-background");
    if (plotBackground == null) {
      hideChartHoverTooltip();
      return;
    }
    Bounds plotBounds = plotBackground.localToScreen(plotBackground.getBoundsInLocal());
    if (plotBounds.contains(event.getScreenX(), event.getScreenY())) {
      return;
    }
    hideChartHoverTooltip();
  }

  /**
   * Ẩn tooltip khi chuột rời khỏi vùng gần điểm bid.
   */
  private void hideChartHoverTooltip() {
    activeHoverPoint = null;
    if (chartInfoPopup != null) {
      chartInfoPopup.setVisible(false);
    }
  }

  /**
   * Cập nhật range trục Y dựa trên các điểm đang hiển thị, thêm padding 15%.
   */
  private void updatePriceAxisRange() {
    if (chartSeries == null || chartSeries.getData().isEmpty()) {
      priceAxis.setAutoRanging(true);
      return;
    }
    priceAxis.setAutoRanging(false);

    double min = chartSeries.getData().stream()
        .mapToDouble(data -> data.getYValue().doubleValue())
        .min()
        .orElse(0);
    double max = chartSeries.getData().stream()
        .mapToDouble(data -> data.getYValue().doubleValue())
        .max()
        .orElse(0);

    double padding = (max - min) * 0.15;
    if (padding == 0) {
      padding = max * 0.1;
    }

    priceAxis.setLowerBound(Math.max(0, Math.floor(min - padding)));
    priceAxis.setUpperBound(Math.ceil(max + padding));
    double range = (max - min) + 2 * padding;
    priceAxis.setTickUnit(Math.max(1, Math.ceil(range / 5)));
  }

  /**
   * Làm rõ đường biểu đồ và các đường trục sau khi JavaFX tạo node chart.
   * Symbol tự động thu nhỏ khi có nhiều điểm dữ liệu.
   */
  private void styleBidChart() {
    if (priceChart == null) {
      return;
    }
    Node line = priceChart.lookup(".chart-series-line");
    if (line != null) {
      line.setStyle("-fx-stroke: #2563eb; -fx-stroke-width: 3px;");
    }

    if (chartSeries != null) {
      int latestIndex = chartSeries.getData().size() - 1;
      for (int i = 0; i < chartSeries.getData().size(); i++) {
        XYChart.Data<String, Number> point = chartSeries.getData().get(i);
        styleBidPoint(point, i == latestIndex);
      }
    }

    Node plotBackground = priceChart.lookup(".chart-plot-background");
    if (plotBackground != null) {
      plotBackground.setStyle("-fx-background-color: #f8fafc;");
    }
    for (Node gridLine : priceChart.lookupAll(".chart-horizontal-grid-lines")) {
      gridLine.setStyle("-fx-stroke: #cbd5e1; -fx-stroke-width: 1.2px;");
    }
    for (Node gridLine : priceChart.lookupAll(".chart-vertical-grid-lines")) {
      gridLine.setStyle("-fx-stroke: #dbe4ef; -fx-stroke-width: 1px;");
    }

    Node horizontalZeroLine = priceChart.lookup(".chart-horizontal-zero-line");
    if (horizontalZeroLine != null) {
      horizontalZeroLine.setStyle("-fx-stroke: #374151;");
    }
    Node verticalZeroLine = priceChart.lookup(".chart-vertical-zero-line");
    if (verticalZeroLine != null) {
      verticalZeroLine.setStyle("-fx-stroke: #374151;");
    }
  }

  /**
   * Style điểm bid trên chart.
   *
   * @param latest true nếu là bid mới nhất
   * @return CSS cho symbol
   */
  private String createChartSymbolStyle(boolean latest) {
    if (latest) {
      return "-fx-background-color: white, #f97316; "
          + "-fx-background-radius: 7px; "
          + "-fx-padding: 7px;";
    }
    return "-fx-background-color: white, #2563eb; "
        + "-fx-background-radius: 5px; "
        + "-fx-padding: 5px;";
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
      stage.setWidth(MAIN_PAGE_WIDTH);
      stage.setHeight(MAIN_PAGE_HEIGHT);
      stage.centerOnScreen();
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
      else if (obj instanceof UpdateAuctionResponse){
        UpdateAuctionResponse update = (UpdateAuctionResponse) obj;
        updateAuction(update);

      }
      else if(obj instanceof AuctionAutoCloseResponse){
        AuctionAutoCloseResponse response = (AuctionAutoCloseResponse) obj;
        if (response.getResponse() && currentAuction != null) {
          LOGGER.info("Nhận AuctionAutoCloseResponse [auctionId: {}]", currentAuction.getId());
          currentAuction.setStatus(AuctionStatus.CLOSED);
          renderClosedAuction(currentAuction);
          bidResultLabel.setText("Phiên đấu giá đã tự động kết thúc");
          bidResultLabel.setStyle(
              "-fx-text-fill: #dc2626; -fx-font-weight: bold; -fx-font-size: 13;");
        }
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

    double minIncrement = 0;
    if (currentAuction != null) {
      minIncrement = currentAuction.getMinimumIncrement();
    }
    double minBid = update.getCurrentPrice() + minIncrement;
    minimumBidLabel.setText("Bid tối thiểu: $" + String.format("%.2f", minBid));

    // 2. GỌI LOGIC TỐI ƯU: Chỉ bóc phần tử cuối cùng của mảng truyền vào đẩy lên UI
    renderBidHistory(update.getBidHistory());
    addLatestBidToChart(update.getBidHistory());
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
