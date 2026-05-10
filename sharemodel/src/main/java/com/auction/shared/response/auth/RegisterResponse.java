package com.auction.shared.response.auth;

import com.auction.shared.model.User;
import com.auction.shared.response.Response;
import java.io.Serializable;

/**
* Kết quả đăng ký user mới.
*/
public class RegisterResponse extends Response implements Serializable {
  private final boolean valid;
  private final User currentUser;

  /**
  * Tạo response đăng ký.
  *
  * @param valid true nếu đăng ký thành công
  * @param currentUser user được tạo mới
  */
  public RegisterResponse(boolean valid, User currentUser) {
    this.valid = valid;
    this.currentUser = currentUser;
  }

  /**
  * Kiểm tra đăng ký có thành công hay không.
  *
  * @return true nếu thành công
  */
  public boolean getResponse() {
    return valid;
  }

  /**
  * Lấy user được tạo mới.
  *
  * @return user mới
  */
  public User getCurrentUser() {
    return currentUser;
  }
}
