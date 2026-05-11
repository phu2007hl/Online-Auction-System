package com.auction.server.service.auth;

import com.auction.server.database.UserDatabase;
import com.auction.shared.model.User;
import com.auction.shared.request.auth.RegisterRequest;
import com.auction.shared.response.auth.RegisterResponse;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Xử lý logic đăng ký user.
*/
public class RegisterAuthentication {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(RegisterAuthentication.class);
  private final RegisterRequest request;
  private static ConcurrentHashMap<String, User> userdata;

  /**
  * Tạo service đăng ký.
  *
  * @param request request đăng ký
  */
  public RegisterAuthentication(RegisterRequest request) {
    this.request = request;
  }

  /**
  * Xác thực đăng ký.
  *
  * @return true nếu đăng ký thành công
  */
  public boolean authenticateRegistration() {
    if (userdata == null) {
      userdata = UserDatabase.getUserData();
      if (userdata == null) {
        userdata = UserDatabase.loadUser();
        UserDatabase.setUserData(userdata);
      }
    }

    User newUser =
        new User(
            request.getEmail(),
            request.getPassword(),
            request.getUsername());
    User result = userdata.putIfAbsent(request.getEmail(), newUser);

    if (result == null) {
      UserDatabase.saveUser(userdata);
      LOGGER.info(
          "User đăng ký thành công: {}",
          request.getUsername());
      return true;
    }
    LOGGER.warn(
        "Đăng ký thất bại - user đã tồn tại: {}",
        request.getEmail());
    return false;
  }

  /**
  * Tạo response đăng ký.
  *
  * @return response đăng ký
  */
  public RegisterResponse createResponse() {
    if (authenticateRegistration()) {
      return new RegisterResponse(true, getUserData());
    }
    return new RegisterResponse(false, null);
  }

  /**
  * Lấy user đã đăng ký.
  *
  * @return user mới
  */
  public User getUserData() {
    return userdata.get(request.getEmail());
  }
}
