package com.auction.shared.response.auth;

import com.auction.shared.response.Response;

/**
* Kết quả đăng nhập admin.
*/
public class AdminLoginResponse extends Response {
  private final boolean valid;

  /**
  * Tạo response đăng nhập admin.
  *
  * @param valid true nếu đăng nhập thành công
  */
  public AdminLoginResponse(boolean valid) {
    this.valid = valid;
  }

  /**
  * Kiểm tra đăng nhập có thành công hay không.
  *
  * @return true nếu thành công
  */
  public boolean getResponse() {
    return valid;
  }
}
