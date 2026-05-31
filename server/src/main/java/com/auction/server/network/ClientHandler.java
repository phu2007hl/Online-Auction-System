package com.auction.server.network;

import com.auction.server.handler.RequestDispatcher;
import com.auction.server.handler.RequestHandler;
import com.auction.shared.model.User;
import com.auction.shared.request.Request;
import com.auction.shared.response.Response;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Xử lý một kết nối client riêng lẻ.
*/
public class ClientHandler implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);

  private Socket connection;
  private ObjectOutputStream out;
  private ObjectInputStream in;
  private RequestDispatcher dispatcher = new RequestDispatcher();
  private static volatile AdminHandler adminHandler;
  private User user;
  private static final Set<ClientHandler> onlineUser = ConcurrentHashMap.newKeySet();

  /**
  * Tạo client handler mới.
  *
  * @param connection socket kết nối
  */
  public ClientHandler(Socket connection) {
    this.connection = connection;
  }

  /**
  * Vòng lặp xử lý request từ client.
  */
  public void run() {
    try {
      out = new ObjectOutputStream(connection.getOutputStream());
      out.flush();
      LOGGER.info("Đã sẵn sàng output stream cho {}", connection.getInetAddress());

      in = new ObjectInputStream(connection.getInputStream());
      LOGGER.info("Đã sẵn sàng input stream cho {}", connection.getInetAddress());

      while (true) {
        Request request = (Request) in.readObject();

        if (request == null) {
          String userContext =
              (user != null) ? user.getUsername() : "unknown";
          LOGGER.error(
              "Nhận được request null từ client: {} [user: {}]",
              connection.getInetAddress(),
              userContext);
          continue;
        }

        String userContext = (user != null) ? user.getUsername() : "unknown";
        LOGGER.info(
            "Đang xử lý request {} cho user: {}",
            request.getClass().getSimpleName(),
            userContext);
        RequestHandler handler = dispatcher.getHandler(request);
        Response response = handler.handle(request, this);

        if (response != null) {
          sendObject(response);
        }
      }
    } catch (Exception e) {
      String userContext = (user != null) ? user.getUsername() : "unknown";
      LOGGER.error("Lỗi trong quá trình xử lý client [user: {}]", userContext, e);
    }
  }

  /**
  * Lấy output stream của client hiện tại.
  *
  * @return output stream
  */
  public ObjectOutputStream getOutputStream() {
    return out;
  }

  /**
  * Gửi object qua output stream của client theo cách tuần tự.
  *
  * @param object object cần gửi
  * @throws IOException nếu không thể ghi dữ liệu
  */
  public void sendObject(Object object) throws IOException {
    if (out == null) {
      throw new IOException("Output stream chưa được khởi tạo");
    }
    synchronized (out) {
      out.writeObject(object);
      out.flush();
      out.reset();
    }
  }

  /**
  * Lấy input stream của client hiện tại.
  *
  * @return input stream
  */
  public ObjectInputStream getInputStream() {
    return in;
  }

  /**
  * Lấy socket kết nối hiện tại.
  *
  * @return socket kết nối
  */
  public Socket getConnection() {
    return connection;
  }

  /**
  * Lấy admin handler đang online.
  *
  * @return admin handler
  */
  public static AdminHandler getAdminHandler() {
    return adminHandler;
  }

  /**
  * Gán admin handler đang online.
  *
  * @param admin admin handler
  */
  public static void setAdminHandler(AdminHandler admin) {
    adminHandler = admin;
  }

  /**
  * Gán user hiện tại cho connection.
  *
  * @param user user hiện tại
  */
  public void setUser(User user) {
    this.user = user;
  }

  /**
  * Lấy user hiện tại.
  *
  * @return user hiện tại
  */
  public User getUser() {
    return user;
  }

  /**
  * Thêm một client vào danh sách online.
  *
  * @param user client cần them
  */
  public static void addOnlineUser(ClientHandler user) {
    onlineUser.add(user);
  }

  /**
  * Xóa một client khỏi danh sách online.
  *
  * @param user client cần xoa
  */
  public static void removeOfflineUser(ClientHandler user) {
    onlineUser.remove(user);
  }

  /**
  * Lấy danh sách client online.
  *
  * @return danh sách client online
  */
  public static Set<ClientHandler> getOnlineUser() {
    return onlineUser;
  }

}
