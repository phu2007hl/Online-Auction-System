package com.auction.shared.request.auction;

import com.auction.shared.model.User;
import com.auction.shared.request.Request;

/**
* Yêu cầu publish auction đã được duyệt ra hệ thống chính.
*/
public class PublishApprovedAuctionRequest extends Request {
  private final CreateAuctionRequest request;
  private final User user;

  /**
  * Tạo request publish auction đã duyệt.
  *
  * @param request request tạo auction cần publish
  */
  public PublishApprovedAuctionRequest(CreateAuctionRequest request,User user) {
    this.request = request;
    this.user = user;
  }

  /**
  * Lấy request tạo auction cần publish.
  *
  * @return request tạo auction
  */
  public CreateAuctionRequest getRequest() {
    return request;
  }
  /**
   * Lấy user tạo request
   * @return User tạo request
   */
  public User getUser(){
    return user;
  }
}
