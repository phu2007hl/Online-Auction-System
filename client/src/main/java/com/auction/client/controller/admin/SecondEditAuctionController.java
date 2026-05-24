package com.auction.client.controller.admin;

import com.auction.client.controller.Controller;
import com.auction.client.network.SocketClient;
import com.auction.shared.auction.Auction;
import com.auction.shared.enums.AuctionStatus;
import com.auction.shared.enums.CreateAuctionStatus;
import com.auction.shared.request.admin.EditAuctionRequest;
import com.auction.shared.request.auction.CreateAuctionRequest;
import com.auction.shared.request.auction.PendingAuctionReviewRequest;
import com.auction.shared.response.admin.EditAuctionResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Controller cho màn hình admin chỉnh sửa request tạo auction đang chờ duyệt.
*/
public class SecondEditAuctionController extends Controller {
  private static final Logger LOGGER = LoggerFactory.getLogger(SecondEditAuctionController.class);

  private SocketClient socket;
  private Auction auction;
  private byte[] imageContent;

  @FXML
  private TextField requestIdField;

  @FXML
  private TextField ownerField;

  @FXML
  private TextField productNameField;

  @FXML
  private ComboBox<String> categoryComboBox;

  @FXML
  private TextField startingPriceField;

  @FXML
  private TextField minimumIncrementField;

  @FXML
  private DatePicker endDatePicker;

  @FXML
  private Label imagePathLabel;

  @FXML
  private ImageView previewImageView;

  @FXML
  private TextArea descriptionArea;

  @FXML
  private Label messageLabel;

  public void setSocketClient(SocketClient socket) {
    this.socket = socket;
    socket.setController(this);
    socket.startListening();
  }

  public void setAuction(Auction auction) {
    this.auction = auction;
    fillForm();
  }

  private void fillForm() {
    if (auction == null) {
      return;
    }

    imageContent = auction.getImageContent();

    requestIdField.setText(String.valueOf(auction.getId()));
    ownerField.setText(auction.getSeller().getUsername());
    productNameField.setText(auction.getItemName());
    categoryComboBox.setValue(auction.getCategory());
    startingPriceField.setText(String.format("%.2f", auction.getStartingPrice()));
    minimumIncrementField.setText(String.format("%.2f", auction.getMinimumIncrement()));
    endDatePicker.setValue(auction.getEndTime());
    descriptionArea.setText(auction.getDescription());

    if (imageContent != null && imageContent.length > 0) {
      previewImageView.setImage(new Image(new ByteArrayInputStream(imageContent)));
      imagePathLabel.setText("Đang dùng ảnh hiện tại");
    }
  }

  @FXML
  private void handleUploadImage(ActionEvent event) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Chọn ảnh sản phẩm");
    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("Tệp ảnh", "*.png", "*.jpg", "*.jpeg"));

    File file = fileChooser.showOpenDialog(null);
    if (file == null) {
      return;
    }

    try {
      imageContent = Files.readAllBytes(file.toPath());
      previewImageView.setImage(new Image(new ByteArrayInputStream(imageContent)));
      imagePathLabel.setText(file.getName());
      LOGGER.info("Admin đã chọn ảnh mới cho pending auction");
    } catch (IOException e) {
      LOGGER.error("Không thể đọc ảnh chỉnh sửa auction", e);
      messageLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
      messageLabel.setText("Không thể đọc ảnh đã chọn");
    }
  }

  @FXML
  private void handleSaveEdit(ActionEvent event) {
    if (auction == null) {
      showError("Không tìm thấy auction cần chỉnh sửa");
      return;
    }

    String itemName = productNameField.getText().trim();
    String category = categoryComboBox.getValue();
    String description = descriptionArea.getText().trim();
    if (itemName.isEmpty()
        || category == null
        || description.isEmpty()
        || imageContent == null
        || imageContent.length == 0) {
      showError("Vui lòng nhập đầy đủ thông tin có thể chỉnh sửa");
      return;
    }

    int requestId = auction.getId();
    EditAuctionRequest editRequest =
        new EditAuctionRequest(
            imageContent,
            requestId,
            category,
            description,
            itemName,
            CreateAuctionStatus.SUCCESS);
    socket.sendRequest(editRequest);
    messageLabel.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
    messageLabel.setText("Đang lưu chỉnh sửa...");
    LOGGER.info("Đã gửi request chỉnh sửa success auction [requestId: {}]", requestId);
  }

  @Override
  public void handle(Object obj) {
    if (obj instanceof EditAuctionResponse response) {
      if (response.getResponse()) {
        messageLabel.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
        messageLabel.setText("Đã lưu chỉnh sửa");
        switchToEditApprovedAuction();
      } else {
        showError("Không thể lưu chỉnh sửa");
      }
    }
  }

  @FXML
  private void switchToEditApprovedAuction(ActionEvent event) {
    switchToEditApprovedAuction();
  }

  private void switchToEditApprovedAuction() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditApprovedAuctionView.fxml"));
      Parent root = loader.load();

      EditApprovedAuctionController controller = loader.getController();
      controller.setSocketClient(socket);

      Stage stage = (Stage) messageLabel.getScene().getWindow();
      stage.getScene().setRoot(root);
      stage.setTitle("Bảng điều chỉnh sửa auction");
      stage.show();
    } catch (IOException e) {
      LOGGER.error("Không thể quay lại bảng chỉnh sửa sau khi chỉnh sửa", e);
    }
  }

  private void showError(String message) {
    messageLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
    messageLabel.setText(message);
  }
}

