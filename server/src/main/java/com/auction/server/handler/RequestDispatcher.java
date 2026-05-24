package com.auction.server.handler;

import com.auction.server.handler.admin.GetCheckedAuctionListHandler;
import com.auction.server.handler.admin.GetPendingAuctionListHandler;
import com.auction.server.handler.admin.EditAuctionHandler;
import com.auction.server.handler.auction.*;
import com.auction.server.handler.auth.AdminLoginHandler;
import com.auction.server.handler.auth.LogOutHandler;
import com.auction.server.handler.auth.LoginRequestHandler;
import com.auction.server.handler.auth.RegisterRequestHandler;
import com.auction.shared.request.Request;
import com.auction.shared.request.admin.GetCheckedAuctionListRequest;
import com.auction.shared.request.admin.EditAuctionRequest;
import com.auction.shared.request.admin.GetPendingAuctionListRequest;
import com.auction.shared.request.auction.*;
import com.auction.shared.request.auth.AdminLoginRequest;
import com.auction.shared.request.auth.LogOutRequest;
import com.auction.shared.request.auth.LoginRequest;
import com.auction.shared.request.auth.RegisterRequest;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Chọn handler phù hợp dựa trên loại request nhận được.
*/
public class RequestDispatcher {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(RequestDispatcher.class);
  private static final Map<Class<? extends Request>, RequestHandler>
      HANDLER_MAP = new HashMap<>();

  static {
    HANDLER_MAP.put(LoginRequest.class, new LoginRequestHandler());
    HANDLER_MAP.put(RegisterRequest.class, new RegisterRequestHandler());
    HANDLER_MAP.put(AdminLoginRequest.class, new AdminLoginHandler());
    HANDLER_MAP.put(CreateAuctionRequest.class, new CreateAuctionRequestHandler());
    HANDLER_MAP.put(GetPendingAuctionListRequest.class, new GetPendingAuctionListHandler());
    HANDLER_MAP.put(GetApprovedAuctionListRequest.class, new GetApprovedAuctionListHandler());
    HANDLER_MAP.put(LogOutRequest.class, new LogOutHandler());
    HANDLER_MAP.put(SaveAuctionReviewResultRequest.class, new SaveAuctionReviewResultHandler());
    HANDLER_MAP.put(
        PublishApprovedAuctionRequest.class,
        new PublishApprovedAuctionHandler());
    HANDLER_MAP.put(AuctionReviewResultRequest.class, new AuctionReviewResultHandler());
    HANDLER_MAP.put(GetCheckedAuctionListRequest.class, new GetCheckedAuctionListHandler());
    HANDLER_MAP.put(BidRequest.class, new BidRequestHandler());
    HANDLER_MAP.put(JoinRoomRequest.class, new JoinRoomRequestHandler());
    HANDLER_MAP.put(GetAuctionDetailRequest.class, new GetAuctionDetailRequestHandler());
    HANDLER_MAP.put(LeaveRoomRequest.class, new LeaveRoomRequestHandler());
    HANDLER_MAP.put(EditAuctionRequest.class, new EditAuctionHandler());
  }

  /**
  * Trả về handler ứng với loại request.
  *
  * @param requestType request cần xử lý
  * @return handler tương ứng
  */
  public static RequestHandler getHandler(Request requestType) {
    LOGGER.debug("Đang điều phối request loại: {}", requestType.getClass().getSimpleName());
    RequestHandler handler = HANDLER_MAP.get(requestType.getClass());
    // Nếu không tìm thấy handler tương ứng thì request đang vượt qua boundary hiện có.
    if (handler == null) {
      LOGGER.error(
          "Không hỗ trợ request loại: {} - không tìm thấy handler",
          requestType.getClass().getSimpleName());
      throw new IllegalArgumentException(
          "Unsupported request type: "
              + requestType.getClass().getSimpleName());
    }
    LOGGER.debug("Đã tìm thấy handler cho request: {}", requestType.getClass().getSimpleName());
    return handler;
  }
}
