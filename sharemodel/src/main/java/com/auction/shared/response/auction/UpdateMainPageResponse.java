package com.auction.shared.response.auction;

import com.auction.shared.response.Response;

/**
* Kết quả cập nhật main page.
*/
public class UpdateMainPageResponse extends Response {
  private final boolean valid;

  /**
  * Tạo response cập nhật main page.
  *
  * @param valid true nếu cập nhật thành công
  */
  public UpdateMainPageResponse(boolean valid) {
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
