package com.auction.server.handler.auction;

import com.auction.server.database.PendingAuctionDatabase;
import com.auction.server.handler.RequestHandler;
import com.auction.server.network.ClientHandler;
import com.auction.server.service.auction.SendToAdminService;
import com.auction.shared.request.Request;
import com.auction.shared.request.auction.CreateAuctionRequest;
import com.auction.shared.request.auction.PendingAuctionReviewRequest;
import com.auction.shared.response.Response;
import com.auction.shared.response.auction.CreateAuctionResponse;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Xử lý request tạo auction mới.
*/
public class CreateAuctionRequestHandler implements RequestHandler {
 


  /**
  * Tạo pending request và forward sang admin hoặc lưu tạm.
  *
  * @param request request tạo auction
  * @param clienthandler client connection hiện tại
  * @return response tạo auction
  */
  @Override
  /**
   * phương thức chính để handle
   */
  public Response handle(Request request, ClientHandler clienthandler) {
        SendToAdminService.sendToAdmin(clienthandler, request);
        return new CreateAuctionResponse(true);
  }

}
