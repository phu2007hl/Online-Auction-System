package com.auction.shared.request.auth;

import com.auction.shared.request.Request;

/**
 * Yêu cầu đăng ký tài khoản user.
 */
public class RegisterRequest extends Request {
  private String email;
  private String password;
  private String username;

  /**
   * Tạo request đăng ký.
   *
   * @param email email đăng ký
   * @param password mật khẩu đăng ký
   * @param username tên hiển thị của user
   */
  public RegisterRequest(String email, String password, String username) {
    this.email = email;
    this.password = password;
    this.username = username;
  }

  /**
   * Lấy email đăng ký.
   *
   * @return email đăng ký
   */
  public String getEmail() {
    return email;
  }

  /**
   * Lấy ten hiển thị.
   *
   * @return ten hiển thị
   */
  public String getUsername() {
    return username;
  }

  /**
   * Lấy mật khẩu đăng ký.
   *
   * @return mật khẩu
   */
  public String getPassword() {
    return password;
  }
}
