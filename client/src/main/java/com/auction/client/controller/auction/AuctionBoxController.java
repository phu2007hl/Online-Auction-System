package com.auction.client.controller.auction;

import com.auction.client.network.SocketClient;
import com.auction.shared.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * Controller của từng ô sản phẩm trên main page.
 */
public class AuctionBoxController {
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
  private Label categoryLabel;

  /**
   * Gán dữ liệu cho ô sản phẩm.
   *
   * @param image ảnh sản phẩm
   * @param startingPrice giá khởi điểm
   * @param category loại sản phẩm
   */
  public void setData(Image image, double startingPrice, String category) {
    imageView.setImage(image);
    priceLabel.setText("Giá: $" + startingPrice);
    categoryLabel.setText("Danh mục: " + category);
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
