package com.auction.server.handler.auction;

import com.auction.server.database.AdminResponseDatabase;
import com.auction.server.handler.RequestHandler;
import com.auction.server.network.ClientHandler;
import com.auction.shared.model.User;
import com.auction.shared.request.Request;
import com.auction.shared.request.auction.AuctionReviewResultRequest;
import com.auction.shared.request.auction.CreateAuctionRequest;
import com.auction.shared.request.auction.PendingAuctionReviewRequest;
import com.auction.shared.request.auction.ToDatabaseRequest;
import com.auction.shared.response.Response;
import com.auction.shared.response.auction.ToDatabaseResponse;
import java.util.HashMap;

/**
* Xử lý request lưu tạm các dữ liệu chưa xử lý xuống database file.
*/
public class ToDatabaseHandler implements RequestHandler {
  /**
  * Lưu request trung gian xuống database file.
  *
  * @param request request cần lưu
  * @param clientHandler client connection hiện tại
  * @return response lưu database
  */
  @Override
  public Response handle(Request request, ClientHandler clientHandler) {
    HashMap<User, Request> adminResponse = AdminResponseDatabase.loadAdminResponse();
    ToDatabaseRequest toDatabaseRequest = (ToDatabaseRequest) request;
    Request wrappedRequest = toDatabaseRequest.getRequest();
    adminResponse.put(extractUser(wrappedRequest), wrappedRequest);
    AdminResponseDatabase.saveAdminResponse(adminResponse);
    return new ToDatabaseResponse(true);
  }

  /**
  * Rút user từ request được bọc trong ToDatabaseRequest.
  *
  * @param request request cần lấy user
  * @return user tương ứng
  */
  private User extractUser(Request request) {
    if (request instanceof PendingAuctionReviewRequest pendingAuctionRequest) {
      return pendingAuctionRequest.getUser();
    }
    if (request instanceof CreateAuctionRequest createAuctionRequest) {
      return createAuctionRequest.getUser();
    }
    if (request instanceof AuctionReviewResultRequest updateUserRequest) {
      return updateUserRequest.getUser();
    }
    throw new IllegalArgumentException(
    "Request does not contain user payload: " + request.getClass().getSimpleName());
  }
}
