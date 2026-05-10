package com.auction.server.handler.admin;

import com.auction.server.database.PendingAuctionDatabase;
import com.auction.server.handler.RequestHandler;
import com.auction.server.network.ClientHandler;
import com.auction.shared.request.Request;
import com.auction.shared.response.Response;
import com.auction.shared.response.admin.GetPendingAuctionListResponse;
import java.util.ArrayList;

/**
* Xử lý request lấy danh sách auction đang chờ duyệt.
*/
public class GetPendingAuctionListHandler implements RequestHandler {
  /**
  * Lấy danh sách auction đang chờ duyệt.
  *
  * @param request request lấy danh sách
  * @param clienthandler client connection hiện tại
  * @return response chứa danh sách pending auction
  */
  @Override
  public Response handle(Request request, ClientHandler clienthandler) {
    ArrayList<Request> requestList = PendingAuctionDatabase.loadRequestList();
    return new GetPendingAuctionListResponse(true, requestList);
  }
}
