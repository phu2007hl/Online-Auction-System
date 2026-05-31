package com.auction.client.controller.auction;

import com.auction.client.network.SocketClient;
import com.auction.client.service.IdGenerator;
import com.auction.shared.model.User;
import com.auction.shared.request.auction.CreateAuctionRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Controller của màn hình tạo auction.
*/
public class CreateAuctionPageController {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(CreateAuctionPageController.class);

  private SocketClient socket;
  private String currentUserName;
  private User currentUser;
  private File selectedImageFile;
  private byte[] imageContent;

  @FXML
  private TextField productNameField;

  @FXML
  private ComboBox<String> categoryComboBox;

  @FXML
  private TextField startingPriceField;

  @FXML
  private DatePicker endDatePicker;

  @FXML
  private Spinner<Integer> endHourSpinner;

  @FXML
  private Spinner<Integer> endMinuteSpinner;

  @FXML
  private TextArea descriptionArea;

  @FXML
  private ToggleButton antiSnippingToggleButton;

  @FXML
  private Label imagePathLabel;

  @FXML
  private Label messageLabel;
  @FXML
  private TextField minimumIncrementField;

  @FXML
  private void initialize() {
    endHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 23));
    endMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 59));
    endHourSpinner.setEditable(true);
    endMinuteSpinner.setEditable(true);
    updateAntiSnippingToggleStyle();
    antiSnippingToggleButton
        .selectedProperty()
        .addListener((observable, oldValue, newValue) -> updateAntiSnippingToggleStyle());
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

  /**
  * Gán socket client cho màn tạo auction.
  *
  * @param socket socket client
  */
  public void setSocketClient(SocketClient socket) {
    this.socket = socket;
  }

  /**
  * Gán tên user hiện tại để quay về main page.
  *
  * @param currentUserName tên user hiện tại
  */
  public void setUserName(String currentUserName) {
    this.currentUserName = currentUserName;
  }

  /**
  * Gán user hiện tại để quay lại main page không mất context.
  *
  * @param currentUser user hiện tại
  */
  public void setCurrentUser(User currentUser) {
    this.currentUser = currentUser;
    if (currentUser != null) {
      this.currentUserName = currentUser.getUsername();
    }
  }

  /**
  * Tải ảnh sản phẩm lên bộ nhớ tạm.
  *
  * @param event su kien click nut
  */
  @FXML
  public void handleUploadImage(ActionEvent event) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Chọn ảnh sản phẩm");
    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("Tệp ảnh", "*.png", "*.jpg", "*.jpeg"));

    File file = fileChooser.showOpenDialog(null);
    if (file != null) {
      selectedImageFile = file;
      imagePathLabel.setText(file.getName());
      try {
        imageContent = Files.readAllBytes(file.toPath());
        LOGGER.info("Đã tải ảnh lên bộ nhớ tạm");
      } catch (IOException e) {
        LOGGER.error("Không thể đọc tệp ảnh", e);
      }
    }
  }

  /**
  * Tạo request tạo auction và gửi lên server.
  *
  * @param event su kien click nut
  */
  @FXML
  public void handleCreateAuction(ActionEvent event) {
    String category = categoryComboBox.getValue();
    String startingPrice = startingPriceField.getText().trim();
    LocalDate endDate = endDatePicker.getValue();
    String description = descriptionArea.getText().trim();
    String itemName = productNameField.getText();
    String minimumIncrement = minimumIncrementField.getText();
    if (category == null
        || selectedImageFile == null
        || imageContent == null
        || imageContent.length == 0
        || startingPrice.isEmpty()
        || endDate == null
        || description.isEmpty()
        || itemName.isEmpty()
        || minimumIncrement.isEmpty()) {
      showError("Vui lòng nhập thông tin của sản phẩm.");
      return;
    }
    int id = IdGenerator.generateId();
    double price;
    double increment;
    try {
      price = Double.parseDouble(startingPrice);
      increment = Double.parseDouble(minimumIncrement);
    } catch (NumberFormatException e) {
      showError("Giá khởi điểm và bước giá tối thiểu phải là số hợp lệ.");
      return;
    }
    if (price <= 0 || increment <= 0) {
      showError("Giá khởi điểm và bước giá tối thiểu phải lớn hơn 0.");
      return;
    }
    LocalDateTime endTime =
        LocalDateTime.of(
            endDate,
            LocalTime.of(endHourSpinner.getValue(), endMinuteSpinner.getValue()));
    if (!endTime.isAfter(LocalDateTime.now())) {
      showError("Thời gian kết thúc phải sau thời gian hiện tại.");
      return;
    }
    CreateAuctionRequest request =
        new CreateAuctionRequest(
            imageContent,
            category,
            price,
            description,
            endTime,
            id,
            itemName,
            increment,
            antiSnippingToggleButton.isSelected()); //Phần này phải thêm minimum increment 
    socket.sendRequest(request);
    LOGGER.info("Đã gửi yêu cầu tạo auction cho sản phẩm {}", productNameField.getText());
    showSuccess("Đã gửi yêu cầu tạo đấu giá");
  }

  /**
  * Quay về main page.
  *
  * @param event su kien click nut
  */
  @FXML
  private void switchToMain(ActionEvent event) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainPageView.fxml"));
      Parent root = loader.load();

      MainPageController controller = loader.getController();
      if (this.currentUser != null) {
        controller.setCurrentUser(this.currentUser);
      } else if (this.currentUserName != null) {
        controller.setUserName(this.currentUserName);
      }
      if (this.socket != null) {
        controller.setSocketClient(this.socket);
      }

      Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
      stage.getScene().setRoot(root);
      stage.setTitle("Hệ thống đấu giá");
      stage.show();
    } catch (IOException e) {
      LOGGER.error("Không thể mở trang chính", e);
    }
  }

  private void showError(String message) {
    messageLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
    messageLabel.setText(message);
  }

  private void showSuccess(String message) {
    messageLabel.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
    messageLabel.setText(message);
  }
}
