package com.auction.shared.request.auth;

import com.auction.shared.request.Request;

/**
 * Yêu cầu đăng nhập của admin.
 */
public class AdminLoginRequest extends Request {
  private String password;

  /**
   * Tạo request đăng nhập admin.
   *
   * @param password mật khẩu admin
   */
  public AdminLoginRequest(String password) {
    this.password = password;
  }

  /**
   * Lấy mật khẩu admin.
   *
   * @return mật khẩu admin
   */
  public String getPassword() {
    return password;
  }
}
