package com.auction.server.handler.auction;

import com.auction.server.database.AuctionListDatabase;
import com.auction.server.database.PendingAuctionDatabase;
import com.auction.server.handler.RequestHandler;
import com.auction.server.network.ClientHandler;
import com.auction.server.service.auction.BroadcastApprovedAuction;
import com.auction.server.service.auction.RemoveRequestService;
import com.auction.server.service.auction.SaveApprovedAuction;
import com.auction.shared.auction.Auction;
import com.auction.shared.request.Request;
import com.auction.shared.request.auction.CreateAuctionRequest;
import com.auction.shared.request.auction.PendingAuctionReviewRequest;
import com.auction.shared.request.auction.PublishApprovedAuctionRequest;
import com.auction.shared.response.Response;
import com.auction.shared.response.auction.UpdateMainPageResponse;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Xử lý request publish auction đã được duyệt.
*/
public class PublishApprovedAuctionHandler implements RequestHandler {


  /**
  * Publish auction ra main page.
  *
  * @param req request publish auction
  * @param clientHandler client connection hiện tại
  * @return response cập nhật main page
  */
  @Override
  public Response handle(Request req, ClientHandler clientHandler) {
    PublishApprovedAuctionRequest request = (PublishApprovedAuctionRequest) req;
    Auction auction = new Auction(request.getRequest().getId(),request.getRequest().getName() ,request.getRequest().getDescription(),request.getUser(),request.getRequest().getStartingPrice() ,5 ,request.getRequest().getEndDate(),request.getRequest().getImageContent(),request.getRequest().getCategory());
    SaveApprovedAuction.saveToDatabase(auction);
    BroadcastApprovedAuction.broadcast(auction, clientHandler);
    RemoveRequestService.removeRequest(request.getRequest().getId());
    return new UpdateMainPageResponse(true);
  }
  
}
