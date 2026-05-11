package com.auction.client.controller.auction;

import com.auction.client.network.SocketClient;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
* Controller của từng ô sản phẩm trên main page.
*/
public class AuctionBoxController {
  private SocketClient socket;

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
  * Gán socket client cho ô sản phẩm.
  *
  * @param socket socket client
  */
  public void setSocketClient(SocketClient socket) {
    this.socket = socket;
  }
}
