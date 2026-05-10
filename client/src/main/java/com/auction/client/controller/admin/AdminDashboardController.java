package com.auction.client.controller.admin;

import com.auction.client.controller.Controller;
import com.auction.client.network.SocketClient;
import com.auction.shared.request.Request;
import com.auction.shared.request.admin.GetPendingAuctionListRequest;
import com.auction.shared.request.auction.AuctionReviewResultRequest;
import com.auction.shared.request.auction.CreateAuctionRequest;
import com.auction.shared.request.auction.PendingAuctionReviewRequest;
import com.auction.shared.request.auction.PublishApprovedAuctionRequest;
import com.auction.shared.request.auction.ToDatabaseRequest;
import com.auction.shared.response.admin.GetPendingAuctionListResponse;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
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
  private TableColumn<RequestRow, Image> pictureColumn;

  @FXML
  private TableColumn<RequestRow, String> categoryColumn;

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
    configurePictureColumn();
    configureCategoryColumn();
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
    CreateAuctionRequest req = pendingRequest.getRequest();
    try {
      socket.sendRequest(new AuctionReviewResultRequest(pendingRequest.getUser(), true));
      socket.sendRequest(new PublishApprovedAuctionRequest(req));
    } catch (Exception e) {
      ToDatabaseRequest request = new ToDatabaseRequest(pendingRequest);
      socket.sendRequest(request);
      LOGGER.error("Không thể gửi kết quả duyệt, đã chuyển sang lưu tạm", e);
    }
    requestRows.remove(row);
  }

  /**
  * Xử lý decline một request.
  *
  * @param row dòng dữ liệu được chọn
  */
  private void handleDecline(RequestRow row) {
    PendingAuctionReviewRequest request = row.getRequest();
    try {
      socket.sendRequest(new AuctionReviewResultRequest(request.getUser(), false));
    } catch (Exception e) {
      LOGGER.error("Không thể gửi kết quả từ chối auction", e);
    }
    requestRows.remove(row);
  }

  /**
  * Dòng dữ liệu hiển thị trong table.
  */
  public static class RequestRow {
    private final Image image;
    private final String category;
    private final PendingAuctionReviewRequest request;

    /**
    * Tạo dòng dữ liệu mới.
    *
    * @param image ảnh sản phẩm
    * @param category loại sản phẩm
    * @param request request cho admin duyệt
    */
    public RequestRow(Image image, String category, PendingAuctionReviewRequest request) {
      this.image = image;
      this.category = category;
      this.request = request;
    }

    /**
    * Lấy ảnh sản phẩm.
    *
    * @return ảnh sản phẩm
    */
    public Image getImage() {
      return image;
    }

    /**
    * Lấy loại sản phẩm.
    *
    * @return loại sản phẩm
    */
    public String getCategory() {
      return category;
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
      ArrayList<Request> requestList = response.getList();
      for (Request request : requestList) {
        if (request instanceof PendingAuctionReviewRequest) {
          addPendingRequest((PendingAuctionReviewRequest) request);
        } else if (request instanceof CreateAuctionRequest) {
          CreateAuctionRequest auctionRequest = (CreateAuctionRequest) request;
          addPendingRequest(
              new PendingAuctionReviewRequest(auctionRequest, auctionRequest.getUser()));
        } else {
          LOGGER.warn(
              "Bỏ qua pending request không đúng kiểu: {}",
              request.getClass().getSimpleName());
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
            new RequestRow(image, auctionRequest.getCategory(), pendingRequest));
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
}
