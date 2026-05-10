package com.auction.shared.response.auth;

import com.auction.shared.response.Response;

/**
* Kết quả đăng xuất.
*/
public class LogOutResponse extends Response {
  private final boolean valid;

  /**
  * Tạo response đăng xuất.
  *
  * @param valid true nếu đăng xuất thành công
  */
  public LogOutResponse(boolean valid) {
    this.valid = valid;
  }

  /**
  * Kiểm tra đăng xuất có thành công hay không.
  *
  * @return true nếu thành công
  */
  public boolean getResponse() {
    return valid;
  }
}
