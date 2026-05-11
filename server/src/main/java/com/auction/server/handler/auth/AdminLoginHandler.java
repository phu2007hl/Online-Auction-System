package com.auction.server.handler.auth;

import com.auction.server.handler.RequestHandler;
import com.auction.server.network.AdminHandler;
import com.auction.server.network.ClientHandler;
import com.auction.server.service.auth.AdminLoginAuthentication;
import com.auction.shared.request.Request;
import com.auction.shared.response.Response;
import com.auction.shared.response.auth.AdminLoginResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Xử lý request đăng nhập admin.
*/
public class AdminLoginHandler implements RequestHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(AdminLoginHandler.class);

  /**
  * Xử lý request đăng nhập admin.
  *
  * @param request request đăng nhập admin
  * @param clienthandler client connection hiện tại
  * @return response đăng nhập admin
  */
  @Override
  public Response handle(Request request, ClientHandler clienthandler) {
    AdminLoginAuthentication adminAuth = new AdminLoginAuthentication(request);
    AdminLoginResponse response = (AdminLoginResponse) adminAuth.createResponse();
    if (response.getResponse()) {
      AdminHandler adminhandler = new AdminHandler(clienthandler);
      ClientHandler.setAdminHandler(adminhandler);
      LOGGER.info("Admin đã đăng nhập thành công");
    } else {
      LOGGER.warn("Admin đăng nhập thất bại");
    }
    return response;
  }
}
