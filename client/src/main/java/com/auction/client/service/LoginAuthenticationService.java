package com.auction.client.service;

import com.auction.shared.request.Request;
import com.auction.shared.request.auth.LoginRequest;

/**
* Service kiểm tra dữ liệu đăng nhập phía client.
*/
public class LoginAuthenticationService {
  private String email;
  private String password;
  private String errorMessage;

  /**
  * Tạo service kiểm tra đăng nhập.
  *
  * @param email email người dùng nhập
  * @param password mật khẩu người dùng nhập
  */
  public LoginAuthenticationService(String email, String password) {
    this.email = email;
    this.password = password;
  }

  /**
  * Lấy email.
  *
  * @return email người dùng nhập
  */
  public String getEmail() {
    return email;
  }

  /**
  * Lấy mật khẩu.
  *
  * @return mật khẩu người dùng nhập
  */
  public String getPassword() {
    return password;
  }

  /**
  * Lấy thông báo lỗi validate.
  *
  * @return thông báo lỗi
  */
  public String getErrorMessage() {
    return errorMessage;
  }

  /**
  * Kiểm tra email có đúng định dạng Gmail không.
  *
  * @return true nếu email hợp lệ
  */
  public boolean getEmailAuthentication() {
    if (!email.contains("@gmail.com") || !email.endsWith("@gmail.com")) {
      return false;
    }
    String beforeAt = email.substring(0, email.indexOf("@"));
    return !beforeAt.isEmpty();
  }

  /**
  * Tạo request đăng nhập nếu dữ liệu hợp lệ.
  *
  * @return request đăng nhập hoặc null nếu dữ liệu không hợp lệ
  */
  public Request createAuthRequest() {
    if (!getEmailAuthentication()) {
      errorMessage = "Email không hợp lệ";
      return null;
    }
    return new LoginRequest(email, password);
  }
}
