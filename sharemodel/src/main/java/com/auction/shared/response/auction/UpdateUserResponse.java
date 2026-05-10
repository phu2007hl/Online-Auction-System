package com.auction.shared.response.auction;

import com.auction.shared.response.Response;

/**
* Kết quả cập nhật thông báo cho user tạo auction.
*/
public class UpdateUserResponse extends Response {
  private final boolean valid;

  /**
  * Tạo response cập nhật user.
  *
  * @param valid true nếu cập nhật thành công
  */
  public UpdateUserResponse(boolean valid) {
    this.valid = valid;
  }

  /**
  * Kiểm tra cập nhật có thành công hay không.
  *
  * @return true nếu thành công
  */
  public boolean getResponse() {
    return valid;
  }
}
