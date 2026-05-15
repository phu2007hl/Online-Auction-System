package com.auction.shared.request.auction;

import com.auction.shared.enums.CreateAuctionStatus;
import com.auction.shared.model.User;
import com.auction.shared.request.Request;

/**
* Yêu cầu thông báo kết quả duyệt auction cho đúng user tạo auction.
*/
public class AuctionReviewResultRequest extends Request {
  private final User user;
  private final boolean valid;
  private CreateAuctionStatus status;
  /**
  * Tạo request gửi kết quả duyệt auction.
  *
  * @param user user cần nhận kết quả
  * @param valid true nếu auction được duyệt, false nếu bị từ chối
  */
  public AuctionReviewResultRequest(User user, boolean valid, CreateAuctionStatus status) {
    this.user = user;
    this.valid = valid;
    this.status = status;
  }

  /**
  * Lấy user cần nhận kết quả.
  *
  * @return user tạo auction
  */
  public User getUser() {
    return user;
  }
  /**
  * Lấy kết quả duyệt từ admin.
  *
  * @return true nếu được duyệt, false nếu bị từ chối
  */

  public boolean getAdminResponse() {
    return valid;
  }
}
