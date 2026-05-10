package com.auction.server.handler.auth;

import com.auction.server.handler.RequestHandler;
import com.auction.server.network.ClientHandler;
import com.auction.server.service.auth.LoginAuthentication;
import com.auction.shared.model.User;
import com.auction.shared.request.Request;
import com.auction.shared.request.auth.LoginRequest;
import com.auction.shared.response.Response;
import com.auction.shared.response.auth.LoginResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Xử lý request đăng nhập user.
*/
public class LoginRequestHandler implements RequestHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(LoginRequestHandler.class);

  /**
  * Xử lý request đăng nhập.
  *
  * @param request request đăng nhập
  * @param clienthandler client connection hiện tại
  * @return response đăng nhập
  */
  @Override
  public Response handle(Request request, ClientHandler clienthandler) {
    LoginRequest loginRequest = (LoginRequest) request;
    LoginAuthentication loginAuth = new LoginAuthentication(request);
    LoginResponse response = (LoginResponse) loginAuth.createResponse();

    // Chi danh dau online sau khi xác thực thành công.
    if (response.getResponse()) {
      User currentUser = loginAuth.getUserData();
      clienthandler.setUser(currentUser);
      ClientHandler.addOnlineUser(clienthandler);
      LOGGER.info("Đăng nhập thành công cho user: {}", currentUser.getUsername());
    } else {
      LOGGER.warn(
          "Đăng nhập thất bại - Email: {}, Status: {}",
          loginRequest.getEmail(),
          response.getStatus());
    }
    return response;
  }
}
