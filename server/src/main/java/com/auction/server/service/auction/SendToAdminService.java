package com.auction.server.service.auction;

import com.auction.server.database.PendingAuctionDatabase;
import com.auction.server.network.AdminHandler;
import com.auction.server.network.ClientHandler;
import com.auction.shared.request.Request;
import com.auction.shared.request.auction.CreateAuctionRequest;
import com.auction.shared.request.auction.PendingAuctionReviewRequest;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendToAdminService {
  private static final Logger LOGGER = LoggerFactory.getLogger(SendToAdminService.class);

  public static void sendToAdmin(ClientHandler clientHandler, Request request) {
    CreateAuctionRequest req = (CreateAuctionRequest) request;
    String userContext = clientHandler.getUser().getUsername();
    PendingAuctionReviewRequest pendingRequest =
        new PendingAuctionReviewRequest(req, clientHandler.getUser());

    PendingAuctionDatabase database = PendingAuctionDatabase.getInstance();
    ConcurrentHashMap<Integer, PendingAuctionReviewRequest> requestList = database.getData();
    requestList.put(pendingRequest.getCreateAuctionRequest().getId(), pendingRequest);
    database.saveData(requestList);

    LOGGER.info(
        "Đã lưu auction request vào pending database [user: {}, category: {}]",
        userContext,
        req.getCategory());

    AdminHandler adminHandler = ClientHandler.getAdminHandler();
    if (adminHandler == null) {
      LOGGER.info("Admin chưa online, chỉ lưu request vào pending database [user: {}]", userContext);
      return;
    }

    boolean forwarded = adminHandler.forwardRequest(pendingRequest, clientHandler);
    if (forwarded) {
      LOGGER.info("Auction request được forward realtime tới admin [user: {}]", userContext);
    } else {
      LOGGER.warn(
          "Không thể forward request tới admin, nhưng request đã được lưu pending [user: {}]",
          userContext);
    }
  }
}
