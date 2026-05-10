package com.auction.server.handler.auth;

import com.auction.server.handler.RequestHandler;
import com.auction.server.network.ClientHandler;
import com.auction.shared.request.Request;
import com.auction.shared.response.Response;
import com.auction.shared.response.auth.LogOutResponse;

/**
* Xử lý request đăng xuất.
*/
public class LogOutHandler implements RequestHandler {
  /**
  * Xử lý request đăng xuất.
  *
  * @param request request đăng xuất
  * @param clientHandler client connection hiện tại
  * @return response đăng xuất
  */
  @Override
  public Response handle(Request request, ClientHandler clientHandler) {
    ClientHandler.removeOfflineUser(clientHandler);
    return new LogOutResponse(true);
  }
}
