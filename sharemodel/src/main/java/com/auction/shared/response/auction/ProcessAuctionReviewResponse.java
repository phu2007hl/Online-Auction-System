package com.auction.shared.response.auction;

import com.auction.shared.response.Response;

/**
 * Response sau khi server xử lý kết quả duyệt auction của admin.
 */
public class ProcessAuctionReviewResponse extends Response {
  private final boolean valid;

  /**
   * Tạo response xử lý kết quả duyệt auction.
   *
   * @param valid true nếu xử lý thành công
   */
  public ProcessAuctionReviewResponse(boolean valid) {
    this.valid = valid;
  }

  /**
   * Kiểm tra xử lý có thành công hay không.
   *
   * @return true nếu thành công
   */
  @Override
  public boolean getResponse() {
    return valid;
  }
}
