package com.auction.server.network;

import com.auction.server.handler.RequestDispatcher;
import com.auction.server.handler.RequestHandler;
import com.auction.shared.model.User;
import com.auction.shared.request.Request;
import com.auction.shared.response.Response;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
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
  private static AdminHandler adminHandler;
  private User user;
  private static HashMap<User, ClientHandler> responseAdmin;
  private static ArrayList<ClientHandler> onlineUser = new ArrayList<>();

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

        out.writeObject(response);
        out.flush();
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
  public static ArrayList<ClientHandler> getOnlineUser() {
    return onlineUser;
  }

  /**
  * Thêm mapping user và client handler phục vụ phản hồi admin.
  *
  * @param user user cần mapping
  * @param clientHandler client handler tương ứng
  */
  public static void addRequest(User user, ClientHandler clientHandler) {
    if (responseAdmin == null) {
      responseAdmin = new HashMap<>();
    }
    responseAdmin.put(user, clientHandler);
  }

  /**
  * Xóa mapping phản hồi admin của một user.
  *
  * @param user user cần xóa mapping
  */
  public static void removeRequest(User user) {
    if (responseAdmin != null) {
      responseAdmin.remove(user);
    }
  }

  /**
  * Lấy mapping user -> client handler.
  *
  * @return map user-client
  */
  public static HashMap<User, ClientHandler> getMap() {
    if (responseAdmin == null) {
      responseAdmin = new HashMap<>();
    }
    return responseAdmin;
  }
}
