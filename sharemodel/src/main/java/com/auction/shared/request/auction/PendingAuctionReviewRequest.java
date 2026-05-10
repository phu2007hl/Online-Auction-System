package com.auction.shared.request.auction;

import com.auction.shared.model.User;
import com.auction.shared.request.Request;

/**
* Request bọc yêu cầu tạo auction kèm user gửi để admin duyệt.
*/
public class PendingAuctionReviewRequest extends Request {
  private final CreateAuctionRequest request;
  private final User user;

  /**
  * Tạo request cho admin duyệt.
  *
  * @param request request tạo auction goc
  * @param user user tạo auction
  */
  public PendingAuctionReviewRequest(CreateAuctionRequest request, User user) {
    this.request = request;
    this.user = user;
  }

  /**
  * Lấy request tạo auction goc.
  *
  * @return request tạo auction
  */
  public CreateAuctionRequest getRequest() {
    return request;
  }

  /**
  * Lấy user tạo auction.
  *
  * @return user tạo auction
  */
  public User getUser() {
    return user;
  }
}
