package com.auction.server.handler.auction;

import com.auction.server.database.AdminResponseDatabase;
import com.auction.server.database.PendingAuctionDatabase;
import com.auction.server.handler.RequestHandler;
import com.auction.server.network.ClientHandler;
import com.auction.server.service.auction.RemoveRequestService;
import com.auction.shared.request.Request;
import com.auction.shared.request.auction.PendingAuctionReviewRequest;
import com.auction.shared.request.auction.SaveAuctionReviewResultRequest;
import com.auction.shared.response.Response;
import com.auction.shared.response.auction.SaveAuctionReviewResultResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
* Xử lý request lưu lịch sử duyệt auction của admin.
*/
public class SaveAuctionReviewResultHandler implements RequestHandler {
  private static final Logger LOGGER =
          LoggerFactory.getLogger(SaveAuctionReviewResultHandler.class);
  /**
  * Lưu kết quả duyệt xuống database file.
  *
  * @param request request cần lưu
  * @param clientHandler client connection hiện tại
  * @return response lưu lịch sử duyệt
  */
  @Override
  public Response handle(Request request, ClientHandler clientHandler) {
    AdminResponseDatabase database = AdminResponseDatabase.getInstance();
    SaveAuctionReviewResultRequest saveRequest = (SaveAuctionReviewResultRequest) request;
    PendingAuctionReviewRequest pendingAuctionReviewRequest = saveRequest.getRequest();
    synchronized (database) {
      LinkedHashMap<Integer, PendingAuctionReviewRequest> adminResponse =
          database.getData();
      adminResponse.put(
          pendingAuctionReviewRequest.getCreateAuctionRequest().getId(),
          pendingAuctionReviewRequest);
      database.saveData(adminResponse);
    }
    RemoveRequestService.removeRequest(pendingAuctionReviewRequest.getCreateAuctionRequest().getId());
    ConcurrentHashMap<Integer, PendingAuctionReviewRequest> requestList =
        PendingAuctionDatabase.getInstance().getData();
    LOGGER.info("Còn lại {} request trong danh sách chờ duyệt", requestList.size());
    return new SaveAuctionReviewResultResponse(true);
  }
}
