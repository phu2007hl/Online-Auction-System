package com.auction.shared.request.auth;

import com.auction.shared.request.Request;

/**
 * Yêu cầu đăng nhập của user.
 */
public class LoginRequest extends Request {
  private String email;
  private String password;

  /**
   * Tạo request đăng nhập.
   *
   * @param email email đăng nhập
   * @param password mật khẩu đăng nhập
   */
  public LoginRequest(String email, String password) {
    this.email = email;
    this.password = password;
  }

  /**
   * Lấy mật khẩu.
   *
   * @return mật khẩu
   */
  public String getPassword() {
    return password;
  }

  /**
   * Lấy email.
   *
   * @return email
   */
  public String getEmail() {
    return email;
  }
}
