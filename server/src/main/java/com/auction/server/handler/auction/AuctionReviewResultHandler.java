package com.auction.server.handler.auction;

import com.auction.server.handler.RequestHandler;
import com.auction.server.network.ClientHandler;
import com.auction.shared.request.Request;
import com.auction.shared.request.auction.AuctionReviewResultRequest;
import com.auction.shared.response.Response;
import com.auction.shared.response.auction.UpdateUserResponse;
import java.util.concurrent.ConcurrentHashMap;
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
    ConcurrentHashMap<String, ClientHandler> auctionRequestSenders =
        ClientHandler.getAuctionRequestSenders();
    AuctionReviewResultRequest req = (AuctionReviewResultRequest) request;
    ClientHandler sender = auctionRequestSenders.get(req.getUser().getEmail());
    if (sender == null) {
      LOGGER.warn(
          "Không tìm thấy client để gửi kết quả duyệt [email: {}]",
          req.getUser().getEmail());
      return new UpdateUserResponse(false);
    }
    try {
      sender.sendObject(req);
    } catch (Exception e) {
      LOGGER.error("Không thể gửi kết quả duyệt về user", e);
      return new UpdateUserResponse(false);
    }
    return new UpdateUserResponse(true);
  }
}
