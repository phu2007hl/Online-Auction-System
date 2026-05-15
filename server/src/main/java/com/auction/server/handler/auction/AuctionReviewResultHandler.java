package com.auction.server.handler.auction;

import com.auction.server.handler.RequestHandler;
import com.auction.server.network.ClientHandler;
import com.auction.shared.model.User;
import com.auction.shared.request.Request;
import com.auction.shared.request.auction.AuctionReviewResultRequest;
import com.auction.shared.response.Response;
import com.auction.shared.response.auction.UpdateUserResponse;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Xử lý kết quả duyệt auction và gửi về đúng user tạo auction.
*/
public class AuctionReviewResultHandler implements RequestHandler {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(AuctionReviewResultHandler.class);

  /**
  * Gửi kết quả duyệt về đúng user tạo auction.
  *
  * @param request request kết quả duyệt
  * @param clienthandler client connection hiện tại
  * @return response cập nhật user
  */
  @Override
  public Response handle(Request request, ClientHandler clienthandler) {
    HashMap<User, ClientHandler> map = ClientHandler.getMap();
    AuctionReviewResultRequest req = (AuctionReviewResultRequest) request;
    try {
      map.get(req.getUser()).getOutputStream().writeObject(req);
    } catch (Exception e) {
          LOGGER.error("Không thể gửi kết quả duyệt về user", e);
    }
    return new UpdateUserResponse(true);
  }
}
