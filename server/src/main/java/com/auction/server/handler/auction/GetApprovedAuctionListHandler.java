package com.auction.server.handler.auction;

import com.auction.server.database.AuctionListDatabase;
import com.auction.server.handler.RequestHandler;
import com.auction.server.network.ClientHandler;
import com.auction.shared.auction.Auction;
import com.auction.shared.request.Request;
import com.auction.shared.response.Response;
import com.auction.shared.response.auction.GetApprovedAuctionListResponse;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Xử lý request lấy danh sách auction đã được duyệt.
*/
public class GetApprovedAuctionListHandler implements RequestHandler {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetApprovedAuctionListHandler.class);

  /**
  * Lấy danh sách auction đã được duyệt.
  *
  * @param request request lấy danh sách
  * @param clientHandler client connection hiện tại
  * @return response chưa danh sách auction
  */
  @Override
  public Response handle(Request request, ClientHandler clientHandler) {
    ConcurrentHashMap<Integer,Auction> auctionList = AuctionListDatabase.getInstance().getData();
    LOGGER.info("Trả danh sách auction đã duyệt [size: {}]", auctionList.size());
    return new GetApprovedAuctionListResponse(true, auctionList);
  }
}
