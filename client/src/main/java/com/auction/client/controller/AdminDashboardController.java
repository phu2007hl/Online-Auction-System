package com.auction.client.controller;

import com.auction.client.controller.AdminDashboardController.RequestRow;
import com.auction.client.network.SocketClient;
import com.auction.shared.request.Request;
import com.auction.shared.request.ToDatabaseRequest;
import com.auction.shared.request.UpdateMainPageRequest;
import com.auction.shared.request.UpdateUserRequest;
import com.auction.shared.request.createAuctionRequest;
import com.auction.shared.request.getAuctionListRequest;
import com.auction.shared.response.UpdateMainPageResponse;
import com.auction.shared.response.createAuctionResponse;
import com.auction.shared.response.getAuctionListResponse;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.ByteArrayInputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminDashboardController extends Controller implements Initializable {
    private SocketClient socket;
    @FXML private TableView<RequestRow>           requestTable;
    @FXML private TableColumn<RequestRow, Image>  pictureColumn;
    @FXML private TableColumn<RequestRow, String> categoryColumn;
    @FXML private TableColumn<RequestRow, Void>   actionColumn;

    private final ObservableList<RequestRow> requestRows =
            FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurePictureColumn();
        configureCategoryColumn();
        configureActionColumn();
        requestTable.setItems(requestRows);
        requestTable.setPlaceholder(new Label("Waiting for auction requests..."));

        
    }

    private void configurePictureColumn() {
        pictureColumn.setCellValueFactory(new PropertyValueFactory<>("image"));
        pictureColumn.setCellFactory(col -> new TableCell<>() {
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

    private void configureCategoryColumn() {
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
    }

    private void configureActionColumn() {
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button acceptBtn  = new Button("Accept");
            private final Button declineBtn = new Button("Decline");
            private final HBox   box        = new HBox(10, acceptBtn, declineBtn);
            {
                box.setAlignment(Pos.CENTER);
                acceptBtn.getStyleClass().addAll("action-btn", "accept-btn");
                declineBtn.getStyleClass().addAll("action-btn", "decline-btn");
                acceptBtn.setOnAction(e -> {
                    RequestRow row = getTableView().getItems().get(getIndex());
                    handleAccept(row);
                });

                declineBtn.setOnAction(e -> {
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
    private void handleAccept(RequestRow row) {
        createAuctionRequest req = row.getRequest();
        try{
            socket.sendRequest(new UpdateUserRequest(req.getUser(),true));
            socket.sendRequest(new UpdateMainPageRequest(req));
        }
        catch (Exception e){
            //Save to database
            //gui mot request de save vao database
            ToDatabaseRequest request = new ToDatabaseRequest(req);
            socket.sendRequest(request);

        }
        requestRows.remove(row); 
    }

    private void handleDecline(RequestRow row) {
        createAuctionRequest req = row.getRequest();
        try{
            socket.sendRequest(new UpdateUserRequest(req.getUser(),false));
            
        }
        catch (Exception e){
            e.printStackTrace();
        }

        requestRows.remove(row);
    }
    public static class RequestRow {

        private final Image                image;
        private final String               category;
        private final createAuctionRequest request;

        public RequestRow(Image image, String category, createAuctionRequest request) {
            this.image    = image;
            this.category = category;
            this.request  = request;
        }
        public Image    getImage()    { return image;    }
        public String   getCategory() { return category; }
        public createAuctionRequest getRequest() { return request; }
    }

    public void setSocketClient(SocketClient socket){
        this.socket = socket;
        socket.setController(this);
        socket.startListening();
        socket.sendRequest(new getAuctionListRequest());
    }
    public void handle(Object obj){
        if (obj instanceof createAuctionRequest){
            createAuctionRequest request = (createAuctionRequest) obj;
            if (request != null){
                Image image = new Image(new ByteArrayInputStream(request.getImageContent()));
                requestRows.add(new RequestRow(image,request.getCategory() , request));


            }

        }
        
        else if (obj instanceof getAuctionListResponse){
            getAuctionListResponse response = (getAuctionListResponse) obj;
            System.out.println("Get response");
            ArrayList<Request> requestList = response.getList();
            for (Request request : requestList){
                System.out.println(request.getClass());
                createAuctionRequest auctionRequest = (createAuctionRequest) request;
                System.out.println("Cast succesfully");
                Image image = new Image(new ByteArrayInputStream(auctionRequest.getImageContent()));
                requestRows.add(new RequestRow(image, auctionRequest.getCategory(), auctionRequest));

            }

        }
    }
}
