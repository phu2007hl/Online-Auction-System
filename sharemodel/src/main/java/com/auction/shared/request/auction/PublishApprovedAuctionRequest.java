package com.auction.shared.request.auction;

import com.auction.shared.request.Request;

/**
* Yêu cầu publish auction đã được duyệt ra hệ thống chính.
*/
public class PublishApprovedAuctionRequest extends Request {
  private final CreateAuctionRequest request;

  /**
  * Tạo request publish auction đã duyệt.
  *
  * @param request request tạo auction cần publish
  */
  public PublishApprovedAuctionRequest(CreateAuctionRequest request) {
    this.request = request;
  }

  /**
  * Lấy request tạo auction cần publish.
  *
  * @return request tạo auction
  */
  public CreateAuctionRequest getRequest() {
    return request;
  }
}
