package com.auction.server.handler.auction;

import com.auction.server.database.PendingAuctionDatabase;
import com.auction.server.handler.RequestHandler;
import com.auction.server.network.ClientHandler;
import com.auction.shared.request.Request;
import com.auction.shared.request.auction.CreateAuctionRequest;
import com.auction.shared.request.auction.PendingAuctionReviewRequest;
import com.auction.shared.response.Response;
import com.auction.shared.response.auction.CreateAuctionResponse;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Xử lý request tạo auction mới.
*/
public class CreateAuctionRequestHandler implements RequestHandler {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(CreateAuctionRequestHandler.class);

  /**
  * Tạo pending request và forward sang admin hoặc lưu tạm.
  *
  * @param request request tạo auction
  * @param clienthandler client connection hiện tại
  * @return response tạo auction
  */
  @Override
  public Response handle(Request request, ClientHandler clienthandler) {
    try {
      CreateAuctionRequest req = (CreateAuctionRequest) request;
      String userContext = clienthandler.getUser().getUsername();
      LOGGER.info(
          "Tạo auction request từ user: {} với category: {}",
          userContext,
          req.getCategory());

      PendingAuctionReviewRequest pendingRequest =
          new PendingAuctionReviewRequest(req, clienthandler.getUser());
      ClientHandler.getAdminHandler()
          .forwardRequest(pendingRequest, clienthandler);
      ClientHandler.addRequest(clienthandler.getUser(), clienthandler);
      LOGGER.info("Auction request được chuyển tiếp tới admin cho user: {}", userContext);
      return new CreateAuctionResponse(true);
    } catch (Exception e) {
      String userContext = clienthandler.getUser().getUsername();
      LOGGER.error(
          "Không thể chuyển request tạo auction tới admin, "
              + "sẽ lưu vào AuctionDatabase [user: {}]",
          userContext,
          e);
      ArrayList<Request> requestList =
          PendingAuctionDatabase.loadRequestList();
      CreateAuctionRequest req = (CreateAuctionRequest) request;
      PendingAuctionReviewRequest pendingRequest =
          new PendingAuctionReviewRequest(req, clienthandler.getUser());
      requestList.add(pendingRequest);
      PendingAuctionDatabase.saveAuctionRequest(requestList);
      LOGGER.info("Auction request đã được lưu vào pending database cho user: {}",
          userContext);
      return new CreateAuctionResponse(true);
    }
  }
}
