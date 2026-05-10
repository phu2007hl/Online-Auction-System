package com.auction.server.handler.auth;

import com.auction.server.handler.RequestHandler;
import com.auction.server.network.ClientHandler;
import com.auction.server.service.auth.RegisterAuthentication;
import com.auction.shared.model.User;
import com.auction.shared.request.Request;
import com.auction.shared.request.auth.RegisterRequest;
import com.auction.shared.response.Response;
import com.auction.shared.response.auth.RegisterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Xử lý request đăng ký user.
*/
public class RegisterRequestHandler implements RequestHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(RegisterRequestHandler.class);

  /**
  * Xử lý request đăng ký.
  *
  * @param request request đăng ký
  * @param clienthandler client connection hiện tại
  * @return response đăng ký
  */
  @Override
  public Response handle(Request request, ClientHandler clienthandler) {
    RegisterRequest req = (RegisterRequest) request;
    RegisterAuthentication registerAuth = new RegisterAuthentication(req);
    RegisterResponse response = (RegisterResponse) registerAuth.createResponse();

    // Chỉ gán user và thêm vào danh sách online khi đăng ký thành công.
    if (response.getResponse()) {
      User currentUser = registerAuth.getUserData();
      clienthandler.setUser(currentUser);
      ClientHandler.addOnlineUser(clienthandler);
      LOGGER.info("Đăng ký thành công cho user: {}", currentUser.getUsername());
    } else {
      LOGGER.warn("Đăng ký thất bại - Email: {}", req.getEmail());
    }
    return response;
  }
}
