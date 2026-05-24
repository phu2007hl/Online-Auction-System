package com.auction.shared.response.auction;

import com.auction.shared.response.Response;

/**
* Kết quả lưu lịch sử duyệt auction của admin.
*/
public class SaveAuctionReviewResultResponse extends Response {
  private final boolean valid;

  /**
  * Tạo response lưu lịch sử duyệt auction.
  *
  * @param valid true nếu lưu thành công
  */
  public SaveAuctionReviewResultResponse(boolean valid) {
    this.valid = valid;
  }

  /**
  * Kiểm tra lưu database có thành công hay không.
  *
  * @return true nếu thành công
  */
  public boolean getResponse() {
    return valid;
  }
}
