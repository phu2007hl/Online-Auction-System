package com.auction.server.service.auction;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auction.server.database.PendingAuctionDatabase;
import com.auction.server.network.ClientHandler;
import com.auction.shared.request.Request;
import com.auction.shared.request.auction.CreateAuctionRequest;
import com.auction.shared.request.auction.PendingAuctionReviewRequest;

public class SavePendingAuctionService {
 private static final Logger LOGGER = LoggerFactory.getLogger(SavePendingAuctionService.class);
 public static void saveToDatabase(Exception e,ClientHandler clientHandler,Request request){
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
