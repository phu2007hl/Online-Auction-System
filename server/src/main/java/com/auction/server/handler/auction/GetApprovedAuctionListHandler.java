package com.auction.server.handler.auction;

import com.auction.server.database.AuctionListDatabase;
import com.auction.server.handler.RequestHandler;
import com.auction.server.network.ClientHandler;
import com.auction.shared.request.Request;
import com.auction.shared.response.Response;
import com.auction.shared.response.auction.GetApprovedAuctionListResponse;
import java.util.ArrayList;

/**
* Xử lý request lấy danh sách auction đã được duyệt.
*/
public class GetApprovedAuctionListHandler implements RequestHandler {
  /**
  * Lấy danh sách auction đã được duyệt.
  *
  * @param request request lấy danh sách
  * @param clientHandler client connection hiện tại
  * @return response chưa danh sách auction
  */
  @Override
  public Response handle(Request request, ClientHandler clientHandler) {
    ArrayList<Request> auctionList = AuctionListDatabase.loadAuctionList();
    return new GetApprovedAuctionListResponse(true, auctionList);
  }
}
