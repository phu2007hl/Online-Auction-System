package com.auction.server.handler.auction;

import com.auction.server.database.AuctionListDatabase;
import com.auction.server.database.PendingAuctionDatabase;
import com.auction.server.handler.RequestHandler;
import com.auction.server.network.ClientHandler;
import com.auction.shared.request.Request;
import com.auction.shared.request.auction.CreateAuctionRequest;
import com.auction.shared.request.auction.PendingAuctionReviewRequest;
import com.auction.shared.request.auction.PublishApprovedAuctionRequest;
import com.auction.shared.response.Response;
import com.auction.shared.response.auction.UpdateMainPageResponse;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Xử lý request publish auction đã được duyệt.
*/
public class PublishApprovedAuctionHandler implements RequestHandler {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(PublishApprovedAuctionHandler.class);

  /**
  * Xóa pending request và publish auction ra main page.
  *
  * @param request request publish auction
  * @param clientHandler client connection hiện tại
  * @return response cập nhật main page
  */
  @Override
  public Response handle(Request request, ClientHandler clientHandler) {
    PublishApprovedAuctionRequest req = (PublishApprovedAuctionRequest) request;
    ArrayList<Request> requestList2 = PendingAuctionDatabase.loadRequestList();
    for (int i = requestList2.size() - 1; i >= 0; i--) {
      Request pendingRequest = requestList2.get(i);
      CreateAuctionRequest createAuctionRequest = extractCreateAuctionRequest(pendingRequest);
      if (createAuctionRequest != null && createAuctionRequest.equals(req.getRequest())) {
        requestList2.remove(i);
      } else {
        LOGGER.debug("Request pending hiện tại không trùng request được duyệt");
      }
    }
    PendingAuctionDatabase.saveAuctionRequest(requestList2);
    ArrayList<Request> requestList3 = PendingAuctionDatabase.loadRequestList();
    LOGGER.info("Còn lại {} request trong danh sách chờ duyệt", requestList3.size());

    for (ClientHandler user : ClientHandler.getOnlineUser()) {
      try {
        user.getOutputStream().writeObject(req.getRequest());
      } catch (Exception e) {
        LOGGER.error("Không thể đẩy auction đã duyệt tới client online", e);
        continue;
      }
    }
    ArrayList<Request> requestList = AuctionListDatabase.loadAuctionList();
    requestList.add(req.getRequest());
    AuctionListDatabase.saveAuction(requestList);

    return new UpdateMainPageResponse(true);
  }

  /**
  * Rút CreateAuctionRequest từ pending request đang lưu.
  *
  * @param request request trong danh sách pending
  * @return request tạo auction nếu dừng kieu
  */
  private CreateAuctionRequest extractCreateAuctionRequest(Request request) {
    if (request instanceof PendingAuctionReviewRequest) {
      PendingAuctionReviewRequest pendingRequest = (PendingAuctionReviewRequest) request;
      return pendingRequest.getRequest();
    }
    if (request instanceof CreateAuctionRequest) {
      return (CreateAuctionRequest) request;
    }
    return null;
  }
}
