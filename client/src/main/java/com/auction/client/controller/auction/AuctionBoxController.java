package com.auction.client.controller.auction;

import com.auction.client.network.SocketClient;
import com.auction.shared.auction.Auction;
import com.auction.shared.enums.AuctionStatus;
import com.auction.shared.model.User;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * Controller của từng ô sản phẩm trên main page.
 */
public class AuctionBoxController {
  private static final DateTimeFormatter END_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

  private SocketClient socket;
  private int auctionId;
  private User currentUser;
  private String currentUserName;
  private MainPageController mainPageController;

  @FXML
  private ImageView imageView;

  @FXML
  private Label priceLabel;

  @FXML
  private Label itemNameLabel;

  @FXML
  private Label sellerLabel;

  @FXML
  private Label endDateLabel;

  @FXML
  private Label categoryLabel;

  @FXML
  private Label statusLabel;

  /**
   * Gán dữ liệu cho ô sản phẩm.
   *
   * @param image ảnh sản phẩm
   * @param auction thông tin auction
   */
  public void setData(Image image, Auction auction) {
    if (image != null) {
      imageView.setImage(image);
    }
    itemNameLabel.setText(auction.getItemName());
    String sellerName = auction.getSeller() == null
        ? "Không xác định"
        : auction.getSeller().getUsername();
    sellerLabel.setText("Người bán: " + sellerName);
    categoryLabel.setText("Danh mục: " + auction.getCategory());
    if (auction.getEndTime() != null) {
      endDateLabel.setText("Kết thúc: " + auction.getEndTime().format(END_TIME_FORMATTER));
    } else {
      endDateLabel.setText("Kết thúc: chưa xác định");
    }
    priceLabel.setText("$" + String.format("%.2f", auction.getCurrentPrice()));
    renderStatus(auction.getStatus());
  }

  private void renderStatus(AuctionStatus status) {
    if (status == AuctionStatus.CLOSED) {
      statusLabel.setText("ĐÃ KẾT THÚC");
      statusLabel.setStyle(
          "-fx-padding: 8 16; -fx-background-radius: 999; "
              + "-fx-background-color: #fef2f2; -fx-text-fill: #dc2626; "
              + "-fx-font-weight: bold; -fx-font-size: 14;");
    } else if (status == AuctionStatus.CANCELLED) {
      statusLabel.setText("ĐÃ HỦY");
      statusLabel.setStyle(
          "-fx-padding: 8 16; -fx-background-radius: 999; "
              + "-fx-background-color: #f3f4f6; -fx-text-fill: #6b7280; "
              + "-fx-font-weight: bold; -fx-font-size: 14;");
    } else {
      statusLabel.setText("ĐANG MỞ");
      statusLabel.setStyle(
          "-fx-padding: 8 16; -fx-background-radius: 999; "
              + "-fx-background-color: #dcfce7; -fx-text-fill: #16a34a; "
              + "-fx-font-weight: bold; -fx-font-size: 14;");
    }
  }

  /**
   * Gán ID phiên đấu giá.
   *
   * @param auctionId ID phiên đấu giá
   */
  public void setAuctionId(int auctionId) {
    this.auctionId = auctionId;
  }

  /**
   * Gán thông tin người dùng hiện tại.
   *
   * @param currentUser người dùng
   * @param currentUserName tên người dùng
   */
  public void setUserInfo(User currentUser, String currentUserName) {
    this.currentUser = currentUser;
    this.currentUserName = currentUserName;
  }

  /**
   * Gán tham chiếu tới MainPageController.
   *
   * @param mainPageController controller trang chính
   */
  public void setMainPageController(MainPageController mainPageController) {
    this.mainPageController = mainPageController;
  }

  /**
   * Gán socket client cho ô sản phẩm.
   *
   * @param socket socket client
   */
  public void setSocketClient(SocketClient socket) {
    this.socket = socket;
  }

  /**
   * Xử lý click vào ô sản phẩm để chuyển sang auction detail.
   *
   * @param event sự kiện chuột
   */
  @FXML
  private void handleClick(MouseEvent event) {
    if (mainPageController != null) {
      mainPageController.joinAuctionRoom(auctionId);
    }
  }
}
