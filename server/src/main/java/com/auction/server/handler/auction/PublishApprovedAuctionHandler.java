package com.auction.server.handler.auction;

import com.auction.server.database.AuctionListDatabase;
import com.auction.server.database.PendingAuctionDatabase;
import com.auction.server.handler.RequestHandler;
import com.auction.server.network.ClientHandler;
import com.auction.server.service.auth.IdGenerator;
import com.auction.shared.auction.Auction;
import com.auction.shared.request.Request;
import com.auction.shared.request.auction.CreateAuctionRequest;
import com.auction.shared.request.auction.PendingAuctionReviewRequest;
import com.auction.shared.request.auction.PublishApprovedAuctionRequest;
import com.auction.shared.response.Response;
import com.auction.shared.response.auction.UpdateMainPageResponse;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

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
    ConcurrentHashMap<Integer,Request> requestList2 = PendingAuctionDatabase.getPendingRequest();
    if (requestList2 == null){
      requestList2 = PendingAuctionDatabase.loadRequestList();
      PendingAuctionDatabase.setPendingRequest(requestList2);
    }
    requestList2.remove(req.getRequest().getId());
    PendingAuctionDatabase.saveAuctionRequest(requestList2);
    ConcurrentHashMap<Integer,Request> requestList3 = PendingAuctionDatabase.loadRequestList();
    LOGGER.info("Còn lại {} request trong danh sách chờ duyệt", requestList3.size());
    Auction auction = new Auction(req.getRequest().getId(),req.getRequest().getName() ,req.getRequest().getDescription(),req.getUser(),req.getRequest().getStartingPrice() ,5 ,req.getRequest().getEndDate(),req.getRequest().getImageContent(),req.getRequest().getCategory());

    for (ClientHandler user : ClientHandler.getOnlineUser()) {
      try {
        user.getOutputStream().writeObject(auction);
      } catch (Exception e) {
        LOGGER.error("Không thể đẩy auction đã duyệt tới client online", e);
        continue;
      }
    }
    ConcurrentHashMap<Integer,Auction> auctionList = AuctionListDatabase.getAuctionList();
    if (auctionList == null){
      auctionList = AuctionListDatabase.loadAuctionList();
      AuctionListDatabase.setAuctionList(auctionList);
    }
    int id = IdGenerator.generateId();
    auctionList.put(id,auction);
    AuctionListDatabase.saveAuction(auctionList);

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
