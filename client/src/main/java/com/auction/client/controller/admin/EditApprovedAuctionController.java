package com.auction.client.controller.admin;

import com.auction.client.controller.Controller;
import com.auction.client.controller.admin.AdminDashboardController.RequestRow;
import com.auction.client.network.SocketClient;
import com.auction.shared.auction.Auction;
import com.auction.shared.enums.AuctionStatus;
import com.auction.shared.enums.CreateAuctionStatus;
import com.auction.shared.request.admin.EditAuctionRequest;
import com.auction.shared.request.admin.GetPendingAuctionListRequest;
import com.auction.shared.request.auction.AuctionReviewResultRequest;
import com.auction.shared.request.auction.CreateAuctionRequest;
import com.auction.shared.request.auction.GetApprovedAuctionListRequest;
import com.auction.shared.request.auction.PendingAuctionReviewRequest;
import com.auction.shared.request.auction.PublishApprovedAuctionRequest;
import com.auction.shared.request.auction.ToDatabaseRequest;
import com.auction.shared.response.admin.GetPendingAuctionListResponse;
import com.auction.shared.response.auction.GetApprovedAuctionListResponse;

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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Controller của dashboard admin.
*/
public class EditApprovedAuctionController extends Controller implements Initializable {
  private static final Logger LOGGER = LoggerFactory.getLogger(AdminDashboardController.class);
  private static boolean updateDashboardSuccess;

  private SocketClient socket;

  @FXML
  private TableView<AuctionRow> requestTable;

  @FXML
  private TableColumn<AuctionRow, Integer> idColumn;

  @FXML
  private TableColumn<AuctionRow, Image> pictureColumn;

  @FXML
  private TableColumn<AuctionRow, String> nameColumn;

  @FXML
  private TableColumn<AuctionRow, String> categoryColumn;

  @FXML
  private TableColumn<AuctionRow, String> ownerColumn;

  @FXML
  private TableColumn<AuctionRow, Double> priceColumn;

  @FXML
  private TableColumn<AuctionRow, Void> actionColumn;

  private final ObservableList<AuctionRow> auctionRows = FXCollections.observableArrayList();

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
    requestTable.setItems(auctionRows);
    requestTable.setPlaceholder(new Label("Đang chờ các phiên đấu giá..."));
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
        private final Button deleteBtn = new Button("Xoá phiên");

          private final Button editBtn = new Button("Chỉnh sửa");
          private final VBox box = new VBox(6,deleteBtn,editBtn);

          {
            box.setAlignment(Pos.CENTER);
            deleteBtn.setPrefWidth(92);
            editBtn.setPrefWidth(92);
            editBtn.setStyle(
                "-fx-background-color: #eab308; -fx-text-fill: white; "
                    + "-fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold;");
            editBtn.setOnAction(
                e -> {
                  AuctionRow row = getTableView().getItems().get(getIndex());
                  switchToEditAuction(row, (Node) e.getSource());
                });
            deleteBtn.setStyle(
                "-fx-background-color: #e81818; -fx-text-fill: white; "
                    + "-fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold;");
            deleteBtn.setOnAction(
                e -> {
                  AuctionRow row = getTableView().getItems().get(getIndex());
                  handleDelete(row);
                });
          }

          @Override
          protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            setAlignment(Pos.CENTER);
            setGraphic(empty ? null : box);
          }
        });
  }

  /**
  * Xử lý accept một request.
  *
  * @param row dòng dữ liệu được chọn
  */

  /**
  * Dòng dữ liệu hiển thị trong table.
  */
  public static class AuctionRow {
    private final int id;
    private final Image image;
    private final String name;
    private final String category;
    private final String owner;
    private final double startingPrice;
    private final Auction auction;

    /**
    * Tạo dòng dữ liệu mới.
    *
    * @param image ảnh sản phẩm
    * @param category loại sản phẩm
    * @param request request cho admin duyệt
    */
    public AuctionRow(
        int id,
        Image image,
        String name,
        String category,
        String owner,
        double startingPrice,
        Auction auction) {
      this.id = id;
      this.image = image;
      this.name = name;
      this.category = category;
      this.owner = owner;
      this.startingPrice = startingPrice;
      this.auction = auction;
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

    public Auction getAuction(){
        return auction;
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
    socket.sendRequest(new GetApprovedAuctionListRequest());
  }

  @Override
  public void handle(Object obj) {
      if (obj instanceof GetApprovedAuctionListResponse) {
      GetApprovedAuctionListResponse response = (GetApprovedAuctionListResponse) obj;
      LOGGER.info("Đã nhận danh sách request chờ duyệt");
      auctionRows.clear();
      ConcurrentHashMap<Integer, Auction> auctionList = response.getAuctionList();
      for (Integer id : auctionList.keySet()) {
        if(auctionList.get(id).getStatus() == AuctionStatus.OPEN){
          addAuction(auctionList.get(id));
          }
      }
    }
  }

  /**
  * Thêm request chờ duyệt vào dashboard nếu dữ liệu hợp lệ.
  *
  * @param pendingRequest request cho admin duyệt
  */
  private void addAuction(Auction auction) {
    if (auction == null) {
      LOGGER.warn("Bỏ qua pending auction rỗng");
      return;
    }
    byte[] imageContent = auction.getImageContent();
    if (imageContent == null || imageContent.length == 0) {
      LOGGER.warn("Bỏ qua auction  thiếu ảnh: {}", auction.getCategory());
      return;
    }

    Image image = new Image(new ByteArrayInputStream(imageContent));
    boolean success =
        auctionRows.add(
            new AuctionRow(
                auction.getId(),
                image,
                auction.getItemName(),
                auction.getCategory(),
                auction.getSeller().getUsername(),
                auction.getStartingPrice(),
                auction));

  }

  
  @FXML
  private void switchToDashBoard(ActionEvent event) {
    try {
      FXMLLoader loader =
              new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
      Parent root = loader.load();

      AdminDashboardController controller = loader.getController();
      controller.setSocketClient(socket);

      Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
      stage.getScene().setRoot(root);
      stage.show();
    } catch (IOException e) {
      LOGGER.error("Không thể mở trang admin dashboard", e);
    }
  }
  private void handleDelete(AuctionRow row){
    Auction auction = row.getAuction();
    try{
        EditAuctionRequest request = new EditAuctionRequest(auction.getImageContent(), auction.getId(), auction.getCategory(), auction.getDescription(),auction.getItemName(), CreateAuctionStatus.SUCCESS,AuctionStatus.CANCELLED);
        socket.sendRequest(request);
    }
    catch (Exception e){
        LOGGER.error("Không thể gửi kết quả từ chối auction", e);
    }
    auctionRows.remove(row);
  }
  @FXML
  private void switchToEditAuction(AuctionRow row, Node source) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SecondEditAuctionView.fxml"));
      Parent root = loader.load();

      SecondEditAuctionController controller = loader.getController();
      controller.setAuction(row.getAuction());
      controller.setSocketClient(socket);

      Stage stage = (Stage) source.getScene().getWindow();
      stage.getScene().setRoot(root);
      stage.setTitle("Chỉnh sửa yêu cầu đấu giá");
      stage.show();
    } catch (IOException e) {
      LOGGER.error("Không thể mở trang chỉnh sửa auction", e);
    }

  }
}
