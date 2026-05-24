package com.auction.client.controller.admin;

import com.auction.client.controller.Controller;
import com.auction.client.network.SocketClient;
import com.auction.shared.request.admin.GetCheckedAuctionListRequest;
import com.auction.shared.request.auction.CreateAuctionRequest;
import com.auction.shared.request.auction.PendingAuctionReviewRequest;
import com.auction.shared.response.admin.GetCheckedAuctionListResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Controller hiển thị lịch sử auction admin đã duyệt hoặc từ chối.
*/
public class CheckedAuctionHistoryController extends Controller implements Initializable {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(CheckedAuctionHistoryController.class);
  private SocketClient socket;

  @FXML
  private TableView<CheckedAuctionRow> historyTable;

  @FXML
  private TableColumn<CheckedAuctionRow, Integer> idColumn;

  @FXML
  private TableColumn<CheckedAuctionRow, Image> pictureColumn;

  @FXML
  private TableColumn<CheckedAuctionRow, String> nameColumn;

  @FXML
  private TableColumn<CheckedAuctionRow, String> categoryColumn;

  @FXML
  private TableColumn<CheckedAuctionRow, String> ownerColumn;

  @FXML
  private TableColumn<CheckedAuctionRow, Double> priceColumn;

  @FXML
  private TableColumn<CheckedAuctionRow, String> statusColumn;

  private final ObservableList<CheckedAuctionRow> checkedRows =
      FXCollections.observableArrayList();

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
    configurePictureColumn();
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
    ownerColumn.setCellValueFactory(new PropertyValueFactory<>("owner"));
    priceColumn.setCellValueFactory(new PropertyValueFactory<>("startingPrice"));
    configureStatusColumn();
    historyTable.setItems(checkedRows);
    historyTable.setPlaceholder(new Label("Chưa có lịch sử duyệt auction"));
  }

  private void configurePictureColumn() {
    pictureColumn.setCellValueFactory(new PropertyValueFactory<>("image"));
    pictureColumn.setCellFactory(
        col -> new TableCell<>() {
          private final ImageView view = new ImageView();

          {
            view.setFitWidth(140);
            view.setFitHeight(90);
            view.setPreserveRatio(true);
          }

          @Override
          protected void updateItem(Image image, boolean empty) {
            super.updateItem(image, empty);
            if (!empty && image != null) {
              view.setImage(image);
              setGraphic(view);
            } else {
              setGraphic(null);
            }
          }
        });
  }

  private void configureStatusColumn() {
    statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    statusColumn.setCellFactory(
        col -> new TableCell<>() {
          @Override
          protected void updateItem(String status, boolean empty) {
            super.updateItem(status, empty);
            if (empty || status == null) {
              setText(null);
              setStyle("");
              return;
            }

            setText(status);
            setAlignment(Pos.CENTER);
            if ("CHẤP NHẬN".equals(status)) {
              setStyle(
                  "-fx-text-fill: #15803d; -fx-font-weight: bold; "
                      + "-fx-background-color: #f0fdf4;");
            } else if ("TỪ CHỐI".equals(status)) {
              setStyle(
                  "-fx-text-fill: #dc2626; -fx-font-weight: bold; "
                      + "-fx-background-color: #fef2f2;");
            } else {
              setStyle("");
            }
          }
        });
  }

  public void setSocketClient(SocketClient socket) {
    this.socket = socket;
    socket.setController(this);
    socket.startListening();
    socket.sendRequest(new GetCheckedAuctionListRequest());
  }

  @Override
  public void handle(Object obj) {
    if (obj instanceof GetCheckedAuctionListResponse response) {
      checkedRows.clear();
      LinkedHashMap<Integer, PendingAuctionReviewRequest> checkedList =
          response.getCheckedRequestList();
      ArrayList<PendingAuctionReviewRequest> requests =
          new ArrayList<>(checkedList.values());
      for (int i = requests.size() - 1; i >= 0; i--) {
        addCheckedRequest(requests.get(i));
      }
    }
  }

  private void addCheckedRequest(PendingAuctionReviewRequest pendingRequest) {
    if (pendingRequest == null || pendingRequest.getCreateAuctionRequest() == null) {
      LOGGER.warn("Bỏ qua checked auction request rỗng");
      return;
    }

    CreateAuctionRequest auctionRequest = pendingRequest.getCreateAuctionRequest();
    byte[] imageContent = auctionRequest.getImageContent();
    Image image = null;
    if (imageContent != null && imageContent.length > 0) {
      image = new Image(new ByteArrayInputStream(imageContent));
    }

    checkedRows.add(
        new CheckedAuctionRow(
            auctionRequest.getId(),
            image,
            auctionRequest.getName(),
            auctionRequest.getCategory(),
            pendingRequest.getUser().getUsername(),
            auctionRequest.getStartingPrice(),
            getDisplayStatus(pendingRequest)));
  }

  private String getDisplayStatus(PendingAuctionReviewRequest pendingRequest) {
    if (pendingRequest.getStatus() == null) {
      return "";
    }
    return switch (pendingRequest.getStatus()) {
      case SUCCESS -> "CHẤP NHẬN";
      case DECLINED -> "TỪ CHỐI";
      default -> pendingRequest.getStatus().name();
    };
  }

  @FXML
  private void switchToAdminDashboard(ActionEvent event) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
      Parent root = loader.load();

      AdminDashboardController controller = loader.getController();
      controller.setSocketClient(socket);

      Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
      stage.setScene(new Scene(root));
      stage.show();
    } catch (IOException e) {
      LOGGER.error("Không thể quay lại dashboard admin", e);
    }
  }

  /**
  * Một dòng dữ liệu trong bảng lịch sử duyệt auction.
  */
  public static class CheckedAuctionRow {
    private final int id;
    private final Image image;
    private final String name;
    private final String category;
    private final String owner;
    private final double startingPrice;
    private final String status;

    public CheckedAuctionRow(
        int id,
        Image image,
        String name,
        String category,
        String owner,
        double startingPrice,
        String status) {
      this.id = id;
      this.image = image;
      this.name = name;
      this.category = category;
      this.owner = owner;
      this.startingPrice = startingPrice;
      this.status = status;
    }

    public int getId() {
      return id;
    }

    public Image getImage() {
      return image;
    }

    public String getName() {
      return name;
    }

    public String getCategory() {
      return category;
    }

    public String getOwner() {
      return owner;
    }

    public double getStartingPrice() {
      return startingPrice;
    }

    public String getStatus() {
      return status;
    }
  }
}
