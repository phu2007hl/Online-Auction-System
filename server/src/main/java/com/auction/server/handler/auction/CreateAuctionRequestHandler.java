package com.auction.server.handler.auction;

import com.auction.server.database.PendingAuctionDatabase;
import com.auction.server.handler.RequestHandler;
import com.auction.server.network.ClientHandler;
import com.auction.shared.request.Request;
import com.auction.shared.request.auction.CreateAuctionRequest;
import com.auction.shared.request.auction.PendingAuctionReviewRequest;
import com.auction.shared.response.Response;
import com.auction.shared.response.auction.CreateAuctionResponse;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Xử lý request tạo auction mới.
*/
public class CreateAuctionRequestHandler implements RequestHandler {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(CreateAuctionRequestHandler.class);
  private CreateAuctionRequest request;  
  private ClientHandler clientHandler;

  /**
  * Tạo pending request và forward sang admin hoặc lưu tạm.
  *
  * @param request request tạo auction
  * @param clienthandler client connection hiện tại
  * @return response tạo auction
  */
  @Override
  /**
   * phương thức chính để handle
   */
  public Response handle(Request request, ClientHandler clienthandler) {
        this.request = (CreateAuctionRequest) request;
        this.clientHandler = clienthandler;
        sendToAdmin();
        return new CreateAuctionResponse(true);
  }
  /**
   * phương thức helper để tách việc xử lí rườm rà ra
   */
  public void sendToAdmin(){
    try {
      PendingAuctionDatabase database = PendingAuctionDatabase.getInstance();
      ConcurrentHashMap<Integer,Request> requestList = database.getData();
      CreateAuctionRequest req = (CreateAuctionRequest) request;
      String userContext = clientHandler.getUser().getUsername();
      LOGGER.info(
          "Tạo auction request từ user: {} với category: {}",
          userContext,
          req.getCategory());

      PendingAuctionReviewRequest pendingRequest =
          new PendingAuctionReviewRequest(req, clientHandler.getUser());
      ClientHandler.getAdminHandler()
              .forwardRequest(pendingRequest, clientHandler);
      ClientHandler.addRequest(clientHandler.getUser(), clientHandler);
      requestList.put(pendingRequest.getRequest().getId(), pendingRequest);
      database.saveData(requestList);
      LOGGER.info("Auction request được chuyển tiếp tới admin cho user: {}", userContext);
  }
  catch (Exception e){
    saveToDatabase(e);

  }

 }
 public void saveToDatabase(Exception e){
    PendingAuctionDatabase database = PendingAuctionDatabase.getInstance();
      String userContext = clientHandler.getUser().getUsername();
      LOGGER.error(
          "Không thể chuyển request tạo auction tới admin, "
              + "sẽ lưu vào AuctionDatabase [user: {}]",
          userContext,
          e);
      ConcurrentHashMap<Integer,Request> requestList = database.getData();
      CreateAuctionRequest req = (CreateAuctionRequest) request;
      PendingAuctionReviewRequest pendingRequest =
          new PendingAuctionReviewRequest(req, clientHandler.getUser());
      requestList.put(pendingRequest.getRequest().getId(), pendingRequest);
      database.saveData(requestList);;
      LOGGER.info("Auction request đã được lưu vào pending database cho user: {}",
          userContext);    
 }
}