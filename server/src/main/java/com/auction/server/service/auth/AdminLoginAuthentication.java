package com.auction.server.service.auth;

import com.auction.shared.request.Request;
import com.auction.shared.request.auth.AdminLoginRequest;
import com.auction.shared.response.Response;
import com.auction.shared.response.auth.AdminLoginResponse;

/**
* Xử lý logic đăng nhập admin.
*/
public class AdminLoginAuthentication {
  private final AdminLoginRequest request;

  /**
  * Tạo service đăng nhập admin.
  *
  * @param request request đăng nhập admin
  */
  public AdminLoginAuthentication(Request request) {
    this.request = (AdminLoginRequest) request;
  }

  /**
  * Kiểm tra mật khẩu admin.
  *
  * @return true nếu dừng mật khẩu
  */
  public boolean matchPassword() {
    return request.getPassword().equals("admin");
  }

  /**
  * Tạo response đăng nhập admin.
  *
  * @return response đăng nhập admin
  */
  public Response createResponse() {
    if (matchPassword()) {
      return new AdminLoginResponse(true);
    }
    return new AdminLoginResponse(false);
  }
}
