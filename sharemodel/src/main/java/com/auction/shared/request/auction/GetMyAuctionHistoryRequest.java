package com.auction.shared.request.auction;

import com.auction.shared.model.User;
import com.auction.shared.request.Request;

/**
 * Request lấy lịch sử auction đã được duyệt của user hiện tại.
 */
public class GetMyAuctionHistoryRequest extends Request {
  private final User user;

  /**
   * Tạo request lấy lịch sử auction đã được duyệt.
   *
   * @param user user hiện tại
   */
  public GetMyAuctionHistoryRequest(User user) {
    this.user = user;
  }

  /**
   * Lấy user hiện tại.
   *
   * @return user hiện tại
   */
  public User getUser() {
    return user;
  }
}
