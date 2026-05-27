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
import java.time.LocalDateTime;
import java.time.LocalTime;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
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
  private Spinner<Integer> endHourSpinner;

  @FXML
  private Spinner<Integer> endMinuteSpinner;

  @FXML
  private ToggleButton antiSnippingToggleButton;

  @FXML
  private Label imagePathLabel;

  @FXML
  private ImageView previewImageView;

  @FXML
  private TextArea descriptionArea;

  @FXML
  private Label messageLabel;

  @FXML
  private void initialize() {
    endHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 23));
    endMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 59));
    updateAntiSnippingToggleStyle();
    antiSnippingToggleButton
        .selectedProperty()
        .addListener((observable, oldValue, newValue) -> updateAntiSnippingToggleStyle());
  }

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
    if (auction.getEndTime() != null) {
      endDatePicker.setValue(auction.getEndTime().toLocalDate());
      endHourSpinner.getValueFactory().setValue(auction.getEndTime().getHour());
      endMinuteSpinner.getValueFactory().setValue(auction.getEndTime().getMinute());
    }
    antiSnippingToggleButton.setSelected(auction.isAntiSnippingEnabled());
    updateAntiSnippingToggleStyle();
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
    LocalDateTime endTime = buildEndTime();
    EditAuctionRequest editRequest =
        new EditAuctionRequest(
            imageContent,
            requestId,
            category,
            description,
            itemName,
            endTime,
            antiSnippingToggleButton.isSelected(),
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

  private LocalDateTime buildEndTime() {
    if (endDatePicker.getValue() == null) {
      return null;
    }
    return LocalDateTime.of(
        endDatePicker.getValue(),
        LocalTime.of(endHourSpinner.getValue(), endMinuteSpinner.getValue()));
  }

  private void updateAntiSnippingToggleStyle() {
    if (antiSnippingToggleButton.isSelected()) {
      antiSnippingToggleButton.setText("Bật");
      antiSnippingToggleButton.setStyle(
          "-fx-background-color: #16a34a; -fx-text-fill: white; "
              + "-fx-font-weight: bold; -fx-background-radius: 999; "
              + "-fx-padding: 6 18; -fx-cursor: hand;");
    } else {
      antiSnippingToggleButton.setText("Tắt");
      antiSnippingToggleButton.setStyle(
          "-fx-background-color: #e5e7eb; -fx-text-fill: #6b7280; "
              + "-fx-font-weight: bold; -fx-background-radius: 999; "
              + "-fx-padding: 6 18; -fx-cursor: hand;");
    }
  }
}
