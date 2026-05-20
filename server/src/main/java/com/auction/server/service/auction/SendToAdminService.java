package com.auction.server.service.auction;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auction.server.database.PendingAuctionDatabase;
import com.auction.server.network.ClientHandler;
import com.auction.shared.request.Request;
import com.auction.shared.request.auction.CreateAuctionRequest;
import com.auction.shared.request.auction.PendingAuctionReviewRequest;

public class SendToAdminService {
   private static final Logger LOGGER = LoggerFactory.getLogger(SendToAdminService.class);
  public static void sendToAdmin(ClientHandler clientHandler,Request request){
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
      ClientHandler.rememberAuctionRequestSender(clientHandler.getUser(), clientHandler);
      requestList.put(pendingRequest.getRequest().getId(), pendingRequest);
      database.saveData(requestList);
      LOGGER.info("Auction request được chuyển tiếp tới admin cho user: {}", userContext);
  }
  catch (Exception e){
    SavePendingAuctionService.saveToDatabase(e, clientHandler, request);

   }
  }
}
