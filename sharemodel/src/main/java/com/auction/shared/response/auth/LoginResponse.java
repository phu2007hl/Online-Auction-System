package com.auction.shared.response.auth;

import com.auction.shared.enums.LoginResponseStatus;
import com.auction.shared.model.User;
import com.auction.shared.response.Response;
import java.io.Serializable;

/**
* Kết quả đăng nhập user.
*/
public class LoginResponse extends Response implements Serializable {
  private final boolean valid;
  private final LoginResponseStatus status;
  private final User currentUser;

  /**
  * Tạo response đăng nhập.
  *
  * @param valid true nếu đăng nhập thành công
  * @param status trạng thái đăng nhập
  * @param currentUser user hiện tại
  */
  public LoginResponse(boolean valid, LoginResponseStatus status, User currentUser) {
    this.valid = valid;
    this.status = status;
    this.currentUser = currentUser;
  }

  /**
  * Kiểm tra đăng nhập có thành công hay không.
  *
  * @return true nếu thành công
  */
  public boolean getResponse() {
    return valid;
  }

  /**
  * Lấy trạng thái đăng nhập.
  *
  * @return trạng thái đăng nhập
  */
  public LoginResponseStatus getStatus() {
    return status;
  }

  /**
  * Lấy user hiện tại.
  *
  * @return user hiện tại
  */
  public User getCurrentUser() {
    return currentUser;
  }
}
