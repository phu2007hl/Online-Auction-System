package com.auction.client.service;

import com.auction.shared.request.Request;
import com.auction.shared.request.auth.RegisterRequest;

/**
* Service kiểm tra dữ liệu đăng ký phía client.
*/
public class RegisterAuthenticationService {
  private String email;
  private String password;
  private String username;
  private String errorMessage;

  /**
  * Tạo service kiểm tra đăng ký.
  *
  * @param email email người dùng nhập
  * @param password mật khẩu người dùng nhập
  * @param username tên người dùng
  */
  public RegisterAuthenticationService(String email, String password, String username) {
    this.email = email;
    this.password = password;
    this.username = username;
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
  * Kiểm tra mật khẩu có đủ độ dài, chữ cái và số không.
  *
  * @return true nếu mật khẩu hợp lệ
  */
  public boolean getPasswordAuthentication() {
    boolean validLength = password.length() >= 8;
    boolean hasLetter = password.matches(".*[a-zA-Z].*");
    boolean hasDigit = password.matches(".*\\d.*");
    return validLength && hasLetter && hasDigit;
  }

  /**
  * Tạo request đăng ký nếu dữ liệu hợp lệ.
  *
  * @return request đăng ký hoặc null nếu dữ liệu không hợp lệ
  */
  public Request createAuthRequest() {
    if (!getEmailAuthentication()) {
      errorMessage = "Email không hợp lệ (phải có dạng abc@gmail.com)";
      return null;
    }
    if (!getPasswordAuthentication()) {
      errorMessage =
          "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ cái và số";
      return null;
    }
    return new RegisterRequest(email, password, username);
  }
}
