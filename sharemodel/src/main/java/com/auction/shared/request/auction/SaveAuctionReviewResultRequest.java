package com.auction.shared.request.auction;

import com.auction.shared.request.Request;

/**
* Yêu cầu lưu kết quả duyệt auction của admin.
*/
public class SaveAuctionReviewResultRequest extends Request {
  private final PendingAuctionReviewRequest request;

  /**
  * Tạo request lưu kết quả duyệt auction.
  *
  * @param request request cần lưu
  */
  public SaveAuctionReviewResultRequest(PendingAuctionReviewRequest request) {
    this.request = request;
  }

  /**
  * Lấy request đã được admin duyệt.
  *
  * @return request đã được admin duyệt
  */
  public PendingAuctionReviewRequest getRequest() {
    return request;
  }
}
