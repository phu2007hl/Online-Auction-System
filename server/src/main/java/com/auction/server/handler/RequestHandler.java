package com.auction.server.handler;

import com.auction.server.network.ClientHandler;
import com.auction.shared.request.Request;
import com.auction.shared.response.Response;

/**
* Giao diện xử lý request trên server.
*/
public interface RequestHandler {
  /**
  * Xử lý request từ client.
  *
  * @param request request cần xử lý
  * @param clientHandler client connection hiện tại
  * @return response trả về client
  */
  Response handle(Request request, ClientHandler clientHandler);
}
