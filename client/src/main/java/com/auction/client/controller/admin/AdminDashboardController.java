package com.auction.client.controller.admin;

import com.auction.client.controller.Controller;
import com.auction.client.network.SocketClient;
import com.auction.shared.enums.CreateAuctionStatus;
import com.auction.shared.request.Request;
import com.auction.shared.request.admin.GetPendingAuctionListRequest;
import com.auction.shared.request.auction.AuctionReviewResultRequest;
import com.auction.shared.request.auction.CreateAuctionRequest;
import com.auction.shared.request.auction.PendingAuctionReviewRequest;
import com.auction.shared.request.auction.PublishApprovedAuctionRequest;
import com.auction.shared.request.auction.ToDatabaseRequest;
import com.auction.shared.response.admin.GetPendingAuctionListResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Controller của dashboard admin.
*/
public class AdminDashboardController extends Controller implements Initializable {
  private static final Logger LOGGER = LoggerFactory.getLogger(AdminDashboardController.class);
  private static boolean updateDashboardSuccess;

  private SocketClient socket;

  @FXML
  private TableView<RequestRow> requestTable;

  @FXML
  private TableColumn<RequestRow, Integer> idColumn;

  @FXML
  private TableColumn<RequestRow, Image> pictureColumn;

  @FXML
  private TableColumn<RequestRow, String> nameColumn;

  @FXML
  private TableColumn<RequestRow, String> categoryColumn;

  @FXML
  private TableColumn<RequestRow, String> ownerColumn;

  @FXML
  private TableColumn<RequestRow, Double> priceColumn;

  @FXML
  private TableColumn<RequestRow, Void> actionColumn;

  private final ObservableList<RequestRow> requestRows = FXCollections.observableArrayList();

  /**
  * Khởi tạo dashboard admin.
  *
  * @param location vi tri FXML
  * @param resources bo tai nguyen FXML
  */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
    configurePictureColumn();
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    configureCategoryColumn();
    ownerColumn.setCellValueFactory(new PropertyValueFactory<>("owner"));
    priceColumn.setCellValueFactory(new PropertyValueFactory<>("startingPrice"));
    configureActionColumn();
    requestTable.setItems(requestRows);
    requestTable.setPlaceholder(new Label("Đang chờ yêu cầu tạo đấu giá..."));
  }

  /**
  * Cấu hình cột ảnh.
  */
  private void configurePictureColumn() {
    pictureColumn.setCellValueFactory(new PropertyValueFactory<>("image"));
    pictureColumn.setCellFactory(
        col -> new TableCell<>() {
          private final ImageView view = new ImageView();

          {
            view.setFitWidth(220);
            view.setFitHeight(140);
            view.setPreserveRatio(true);
          }

          @Override
          protected void updateItem(Image img, boolean empty) {
            super.updateItem(img, empty);
            if (!empty && img != null) {
              view.setImage(img);
              setGraphic(view);
            } else {
              setGraphic(null);
            }
          }
        });
  }

  /**
  * Cau hinh cot category.
  */
  private void configureCategoryColumn() {
    categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
  }

  /**
  * Cau hinh nut accept/decline.
  */
  private void configureActionColumn() {
    actionColumn.setCellFactory(
        col -> new TableCell<>() {
          private final Button acceptBtn = new Button("Duyệt");
          private final Button declineBtn = new Button("Từ chối");
          private final HBox box = new HBox(10, acceptBtn, declineBtn);

          {
            box.setAlignment(Pos.CENTER);
            acceptBtn.getStyleClass().addAll("action-btn", "accept-btn");
            declineBtn.getStyleClass().addAll("action-btn", "decline-btn");
            acceptBtn.setOnAction(
                e -> {
                  RequestRow row = getTableView().getItems().get(getIndex());
                  handleAccept(row);
                });

            declineBtn.setOnAction(
                e -> {
                  RequestRow row = getTableView().getItems().get(getIndex());
                  handleDecline(row);
                });
          }

          @Override
          protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(empty ? null : box);
          }
        });
  }

  /**
  * Xử lý accept một request.
  *
  * @param row dòng dữ liệu được chọn
  */
  private void handleAccept(RequestRow row) {
    PendingAuctionReviewRequest pendingRequest = row.getRequest();
    pendingRequest.setStatus(CreateAuctionStatus.SUCCESS);
    CreateAuctionRequest req = pendingRequest.getRequest();
    try {
      socket.sendRequest(new AuctionReviewResultRequest(pendingRequest.getUser(), true, CreateAuctionStatus.SUCCESS));
      socket.sendRequest(new PublishApprovedAuctionRequest(req,pendingRequest.getUser()));
      LOGGER.info("ADMIN: Đã gửi request chấp nhận auction đến client tương ứng và broadcast đến tất cả user");

      ToDatabaseRequest request = new ToDatabaseRequest(pendingRequest);
      socket.sendRequest(request);
      LOGGER.info("Đã gửi request lưu auction SUCCESS vào database");
    } catch (Exception e) {
      LOGGER.error("Không thể gửi kết quả duyệt auction", e);
    }
    requestRows.remove(row);
  }

  /**
  * Xử lý decline một request.
  *
  * @param row dòng dữ liệu được chọn
  */
  private void handleDecline(RequestRow row) {
    PendingAuctionReviewRequest pendingRequest = row.getRequest();
    pendingRequest.setStatus(CreateAuctionStatus.DECLINED);
    try {
      socket.sendRequest(new AuctionReviewResultRequest(pendingRequest.getUser(), false, CreateAuctionStatus.DECLINED));
      LOGGER.info("ADMIN: Đã gửi request từ chối auction đến client tương ứng");

      ToDatabaseRequest request = new ToDatabaseRequest(pendingRequest);
      socket.sendRequest(request);
      LOGGER.info("Đã gửi request lưu auction DECLINED vào database");
    } catch (Exception e) {
      LOGGER.error("Không thể gửi kết quả từ chối auction", e);
    }
    requestRows.remove(row);
  }

  /**
  * Dòng dữ liệu hiển thị trong table.
  */
  public static class RequestRow {
    private final int id;
    private final Image image;
    private final String name;
    private final String category;
    private final String owner;
    private final double startingPrice;
    private final PendingAuctionReviewRequest request;

    /**
    * Tạo dòng dữ liệu mới.
    *
    * @param image ảnh sản phẩm
    * @param category loại sản phẩm
    * @param request request cho admin duyệt
    */
    public RequestRow(
        int id,
        Image image,
        String name,
        String category,
        String owner,
        double startingPrice,
        PendingAuctionReviewRequest request) {
      this.id = id;
      this.image = image;
      this.name = name;
      this.category = category;
      this.owner = owner;
      this.startingPrice = startingPrice;
      this.request = request;
    }

    public int getId() {
      return id;
    }

    /**
    * Lấy ảnh sản phẩm.
    *
    * @return ảnh sản phẩm
    */
    public Image getImage() {
      return image;
    }

    public String getName() {
      return name;
    }

    /**
    * Lấy loại sản phẩm.
    *
    * @return loại sản phẩm
    */
    public String getCategory() {
      return category;
    }

    public String getOwner() {
      return owner;
    }

    public double getStartingPrice() {
      return startingPrice;
    }

    /**
    * Lấy request cho admin duyệt.
    *
    * @return request cho admin duyệt
    */
    public PendingAuctionReviewRequest getRequest() {
      return request;
    }
  }

  /**
  * Gán socket client và tải danh sách pending auction.
  *
  * @param socket socket client
  */
  public void setSocketClient(SocketClient socket) {
    this.socket = socket;
    socket.setController(this);
    socket.startListening();
    socket.sendRequest(new GetPendingAuctionListRequest());
  }

  @Override
  public void handle(Object obj) {
    if (obj instanceof PendingAuctionReviewRequest) {
      PendingAuctionReviewRequest request = (PendingAuctionReviewRequest) obj;
      addPendingRequest(request);
    } else if (obj instanceof GetPendingAuctionListResponse) {
      GetPendingAuctionListResponse response = (GetPendingAuctionListResponse) obj;
      LOGGER.info("Đã nhận danh sách request chờ duyệt");
      requestRows.clear();
      ConcurrentHashMap<Integer,Request> requestList = response.getList();
      for (Integer id : requestList.keySet()) {
        if (requestList.get(id) instanceof PendingAuctionReviewRequest) {
          addPendingRequest((PendingAuctionReviewRequest) requestList.get(id));
        } else if (requestList.get(id) instanceof CreateAuctionRequest) {
          CreateAuctionRequest auctionRequest = (CreateAuctionRequest) requestList.get(id);
          addPendingRequest(
              new PendingAuctionReviewRequest(auctionRequest, auctionRequest.getUser()));
        } else {
          LOGGER.warn(
              "Bỏ qua pending request không đúng kiểu: {}",
              requestList.get(id).getClass().getSimpleName());
        }
      }
    }
  }

  /**
  * Thêm request chờ duyệt vào dashboard nếu dữ liệu hợp lệ.
  *
  * @param pendingRequest request cho admin duyệt
  */
  private void addPendingRequest(PendingAuctionReviewRequest pendingRequest) {
    if (pendingRequest == null || pendingRequest.getRequest() == null) {
      LOGGER.warn("Bỏ qua pending auction request rỗng");
      return;
    }

    CreateAuctionRequest auctionRequest = pendingRequest.getRequest();
    byte[] imageContent = auctionRequest.getImageContent();
    if (imageContent == null || imageContent.length == 0) {
      LOGGER.warn("Bỏ qua auction request thiếu ảnh: {}", auctionRequest.getCategory());
      return;
    }

    Image image = new Image(new ByteArrayInputStream(imageContent));
    boolean success =
        requestRows.add(
            new RequestRow(
                auctionRequest.getId(),
                image,
                auctionRequest.getName(),
                auctionRequest.getCategory(),
                pendingRequest.getUser().getUsername(),
                auctionRequest.getStartingPrice(),
                pendingRequest));
    updateDashboardSuccess = success;
  }

  /**
  * Lấy trạng thái cập nhật dashboard.
  *
  * @return true nếu cập nhật thành công
  */
  public static boolean getUpdateSuccess() {
    return updateDashboardSuccess;
  }
  @FXML
  private void switchToCheckedAuctionHistory(ActionEvent event) {
    try {
      FXMLLoader loader =
              new FXMLLoader(getClass().getResource("/fxml/CheckedAuctionHistoryView.fxml"));
      Parent root = loader.load();

      CheckedAuctionHistoryController controller = loader.getController();
      controller.setSocketClient(socket);

      Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
      stage.getScene().setRoot(root);
      stage.show();
    } catch (IOException e) {
      LOGGER.error("Không thể mở trang lịch sử duyệt auction", e);
    }
  }
}
