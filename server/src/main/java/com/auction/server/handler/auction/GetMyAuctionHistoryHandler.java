package com.auction.server.handler.auction;

import com.auction.server.database.AdminResponseDatabase;
import com.auction.server.handler.RequestHandler;
import com.auction.server.network.ClientHandler;
import com.auction.shared.model.User;
import com.auction.shared.request.Request;
import com.auction.shared.request.auction.GetMyAuctionHistoryRequest;
import com.auction.shared.request.auction.PendingAuctionReviewRequest;
import com.auction.shared.response.Response;
import com.auction.shared.response.auction.GetMyAuctionHistoryResponse;
import java.util.LinkedHashMap;

/**
 * Handler lấy lịch sử auction đã được admin xử lý của user hiện tại.
 */
public class GetMyAuctionHistoryHandler implements RequestHandler {

  /**
   * Lọc AdminResponseDatabase theo user hiện tại.
   *
   * @param request request lấy lịch sử auction
   * @param clientHandler client hiện tại
   * @return response danh sách auction đã duyệt
   */
  @Override
  public Response handle(Request request, ClientHandler clientHandler) {
    GetMyAuctionHistoryRequest historyRequest =
        (GetMyAuctionHistoryRequest) request;
    User user = historyRequest.getUser();
    LinkedHashMap<Integer, PendingAuctionReviewRequest> myAuctionHistory =
        new LinkedHashMap<>();

    if (user == null || user.getEmail() == null) {
      return new GetMyAuctionHistoryResponse(false, myAuctionHistory);
    }

    AdminResponseDatabase database = AdminResponseDatabase.getInstance();
    LinkedHashMap<Integer, PendingAuctionReviewRequest> checkedRequestList;
    synchronized (database) {
      checkedRequestList = new LinkedHashMap<>(database.getData());
    }

    for (Integer requestId : checkedRequestList.keySet()) {
      PendingAuctionReviewRequest pendingRequest = checkedRequestList.get(requestId);
      if (isAuctionOfUser(pendingRequest, user)) {
        myAuctionHistory.put(requestId, pendingRequest);
      }
    }

    return new GetMyAuctionHistoryResponse(true, myAuctionHistory);
  }

  private boolean isAuctionOfUser(
      PendingAuctionReviewRequest pendingRequest,
      User user) {
    if (pendingRequest == null || pendingRequest.getUser() == null) {
      return false;
    }
    return user.getEmail().equals(pendingRequest.getUser().getEmail());
  }
}
