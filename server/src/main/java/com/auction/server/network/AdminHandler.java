package com.auction.server.network;

import com.auction.shared.request.Request;
import com.auction.shared.response.Response;
import java.io.ObjectInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Lớp bao phủ connection admin de forward request.
*/
public class AdminHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(AdminHandler.class);

  private final ClientHandler adminClientHandler;
  private final ObjectInputStream in;

  /**
  * Tạo admin handler từ connection hiện có.
  *
  * @param clienthandler client handler của admin
  */
  public AdminHandler(ClientHandler clienthandler) {
    this.adminClientHandler = clienthandler;
    this.in = clienthandler.getInputStream();
  }

  /**
  * Forward request toi admin.
  *
  * @param request request cần forward
  * @param clientHandler client connection goc
  */
  public boolean forwardRequest(Request request, ClientHandler clientHandler) {
    try {
      String userContext =
          (clientHandler.getUser() != null)
              ? clientHandler.getUser().getUsername()
              : "unknown";
      adminClientHandler.sendObject(request);
      LOGGER.info(
          "Đã chuyển request {} tới admin [user: {}]",
          request.getClass().getSimpleName(),
          userContext);
      return true;
    } catch (Exception e) {
      String userContext =
          (clientHandler.getUser() != null)
              ? clientHandler.getUser().getUsername()
              : "unknown";
      LOGGER.error(
          "Không thể chuyển request {} tới admin [user: {}]",
          request.getClass().getSimpleName(),
          userContext,
          e);
      return false;
    }
  }

  /**
  * Đọc response từ admin.
  *
  * @return response từ admin
  */
  public Response getAdminResponse() {
    try {
      LOGGER.debug("Đang đọc response từ admin");
      return (Response) in.readObject();
    } catch (Exception e) {
      LOGGER.error("Không thể đọc response từ admin", e);
      return null;
    }
  }
}
