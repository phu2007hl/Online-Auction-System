package com.auction.server.handler.auction;

import com.auction.server.database.AdminResponseDatabase;
import com.auction.server.database.AuctionListDatabase;
import com.auction.server.database.PendingAuctionDatabase;
import com.auction.server.handler.RequestHandler;
import com.auction.server.network.ClientHandler;
import com.auction.server.service.auction.BroadcastApprovedAuction;
import com.auction.shared.auction.Auction;
import com.auction.shared.enums.CreateAuctionStatus;
import com.auction.shared.request.Request;
import com.auction.shared.request.auction.CreateAuctionRequest;
import com.auction.shared.request.auction.PendingAuctionReviewRequest;
import com.auction.shared.request.auction.ProcessAuctionReviewRequest;
import com.auction.shared.response.Response;
import com.auction.shared.response.auction.SaveAuctionReviewResultResponse;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Xử lý toàn bộ transaction duyệt auction của admin.
 */
public class ProcessAuctionReviewHandler implements RequestHandler {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(ProcessAuctionReviewHandler.class);

  /**
   * Lưu history, publish nếu được duyệt, xóa pending và gửi kết quả về user.
   *
   * @param request request xử lý duyệt auction
   * @param clientHandler client admin hiện tại
   * @return response kết quả xử lý
   */
  @Override
  public Response handle(Request request, ClientHandler clientHandler) {
    ProcessAuctionReviewRequest reviewRequest = (ProcessAuctionReviewRequest) request;
    PendingAuctionReviewRequest pendingRequest = reviewRequest.getPendingRequest();
    CreateAuctionStatus status = reviewRequest.getStatus();
    pendingRequest.setStatus(status);

    Auction auction = null;
    synchronized (ProcessAuctionReviewHandler.class) {
      saveReviewHistory(pendingRequest);

      if (status == CreateAuctionStatus.SUCCESS) {
        auction = saveApprovedAuction(pendingRequest);
      }

      removePendingRequest(pendingRequest);
    }

    if (auction != null) {
      BroadcastApprovedAuction.broadcast(auction, clientHandler);
    }

    LOGGER.info(
        "Đã xử lý kết quả duyệt auction [requestId: {}, status: {}]",
        pendingRequest.getCreateAuctionRequest().getId(),
        status);
    return new SaveAuctionReviewResultResponse(true);
  }

  private void saveReviewHistory(PendingAuctionReviewRequest pendingRequest) {
    AdminResponseDatabase database = AdminResponseDatabase.getInstance();
    synchronized (database) {
      LinkedHashMap<Integer, PendingAuctionReviewRequest> adminResponse = database.getData();
      int requestId = pendingRequest.getCreateAuctionRequest().getId();
      adminResponse.put(requestId, pendingRequest);
      database.saveData(adminResponse);
    }
  }

  private Auction saveApprovedAuction(PendingAuctionReviewRequest pendingRequest) {
    CreateAuctionRequest request = pendingRequest.getCreateAuctionRequest();
    Auction auction =
        new Auction(
            request.getId(),
            request.getName(),
            request.getDescription(),
            pendingRequest.getUser(),
            request.getStartingPrice(),
            request.getMinimumIncrement(),
            request.getEndTime(),
            request.getImageContent(),
            request.getCategory());
    auction.setAntiSnippingEnabled(request.isAntiSnippingEnabled());

    AuctionListDatabase database = AuctionListDatabase.getInstance();
    synchronized (database) {
      ConcurrentHashMap<Integer, Auction> auctionList = database.getData();
      auctionList.put(auction.getId(), auction);
      database.saveData(auctionList);
    }
    return auction;
  }

  private void removePendingRequest(PendingAuctionReviewRequest pendingRequest) {
    PendingAuctionDatabase database = PendingAuctionDatabase.getInstance();
    synchronized (database) {
      ConcurrentHashMap<Integer, PendingAuctionReviewRequest> requestList = database.getData();
      int requestId = pendingRequest.getCreateAuctionRequest().getId();
      requestList.remove(requestId);
      database.saveData(requestList);
      LOGGER.info("Còn lại {} request trong danh sách chờ duyệt", requestList.size());
    }
  }
}
