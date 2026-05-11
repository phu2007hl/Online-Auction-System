package com.auction.server.service.auth;

import com.auction.server.database.UserDatabase;
import com.auction.shared.enums.LoginResponseStatus;
import com.auction.shared.model.User;
import com.auction.shared.request.Request;
import com.auction.shared.request.auth.LoginRequest;
import com.auction.shared.response.Response;
import com.auction.shared.response.auth.LoginResponse;
import java.util.concurrent.ConcurrentHashMap;

/**
* Xử lý logic đăng nhập user.
*/
public class LoginAuthentication {
  private final LoginRequest request;
  private ConcurrentHashMap<String, User> userdata;

  /**
  * Tạo service đăng nhập.
  *
  * @param request request đăng nhập
  */
  public LoginAuthentication(Request request) {
    this.request = (LoginRequest) request;
    userdata = UserDatabase.getUserData();
    if (userdata == null) {
      userdata = UserDatabase.loadUser();
    }
  }

  /**
  * Kiểm tra email có tồn tại hay không.
  *
  * @return true nếu email đã tồn tại
  */
  public boolean containsEmail() {
    return userdata.containsKey(request.getEmail());
  }

  /**
  * Kiểm tra mật khẩu có khớp không.
  *
  * @return true nếu mật khẩu khớp
  */
  public boolean matchPassword() {
    String userPassword = userdata.get(request.getEmail()).getPassword();
    String requestPassword = request.getPassword();
    return requestPassword.equals(userPassword);
  }

  /**
  * Tạo response đăng nhập.
  *
  * @return response đăng nhập
  */
  public Response createResponse() {
    if (containsEmail()) {
      if (!matchPassword()) {
        return new LoginResponse(false, LoginResponseStatus.INVALID_PASSWORD, null);
      }
      return new LoginResponse(true, LoginResponseStatus.SUCCESS, getUserData());
    }
    return new LoginResponse(false, LoginResponseStatus.EMAIL_NOT_FOUND, null);
  }

  /**
  * Lấy thông tin user đăng nhập.
  *
  * @return user hiện tại
  */
  public User getUserData() {
    return userdata.get(request.getEmail());
  }
}
