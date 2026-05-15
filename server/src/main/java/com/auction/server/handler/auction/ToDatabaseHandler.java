package com.auction.server.handler.auction;

import com.auction.server.database.AdminResponseDatabase;
import com.auction.server.database.PendingAuctionDatabase;
import com.auction.server.handler.RequestHandler;
import com.auction.server.network.ClientHandler;
import com.auction.shared.request.Request;
import com.auction.shared.request.auction.PendingAuctionReviewRequest;
import com.auction.shared.request.auction.ToDatabaseRequest;
import com.auction.shared.response.Response;
import com.auction.shared.response.auction.ToDatabaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
* Xử lý request lưu các dữ liệu duyệt auction xuống database file.
*/
public class ToDatabaseHandler implements RequestHandler {
  private static final Logger LOGGER =
          LoggerFactory.getLogger(ToDatabaseHandler.class);
  /**
  * Lưu request trung gian xuống database file.
  *
  * @param request request cần lưu
  * @param clientHandler client connection hiện tại
  * @return response lưu database
  */
  @Override
  public Response handle(Request request, ClientHandler clientHandler) {
    LinkedHashMap<Integer, PendingAuctionReviewRequest> adminResponse =
        AdminResponseDatabase.loadAdminResponse();
    ToDatabaseRequest toDatabaseRequest = (ToDatabaseRequest) request;
    PendingAuctionReviewRequest pendingAuctionReviewRequest = toDatabaseRequest.getRequest();
    adminResponse.put(pendingAuctionReviewRequest.getRequest().getId(), pendingAuctionReviewRequest);
    AdminResponseDatabase.saveAdminResponse(adminResponse);
    PendingAuctionDatabase.removeRequest(pendingAuctionReviewRequest.getRequest().getId());
    ConcurrentHashMap<Integer,Request> requestList = PendingAuctionDatabase.loadRequestList();
    LOGGER.info("Còn lại {} request trong danh sách chờ duyệt", requestList.size());
    return new ToDatabaseResponse(true);
  }
}
