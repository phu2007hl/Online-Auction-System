package com.auction.client.network;

import com.auction.client.controller.Controller;
import com.auction.shared.request.Request;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Lớp giao tiếp socket của client.
*/
public class SocketClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(SocketClient.class);

  private final Socket socket;
  private final ObjectOutputStream out;
  private final ObjectInputStream in;
  private Controller controller;
  private Thread listenerThread;
  private volatile boolean listening;

  /**
  * Tạo client socket và kết nối tới server.
  *
  * @param port cổng server
  */
  public SocketClient(int port) {
    Socket createdSocket = null;
    ObjectOutputStream createdOut = null;
    ObjectInputStream createdIn = null;

    try {
      createdSocket = new Socket("local host", port);
      LOGGER.info("Đã kết nối tới server ở cổng {}", port);

      createdOut = new ObjectOutputStream(createdSocket.getOutputStream());
      createdOut.flush();
      createdIn = new ObjectInputStream(createdSocket.getInputStream());
    } catch (Exception e) {
      LOGGER.error("Không thể khởi tạo kết nối client", e);
    }

    socket = createdSocket;
    out = createdOut;
    in = createdIn;
  }

  /**
  * Gán controller đang nhận response.
  *
  * @param controller controller hiện tại
  */
  public void setController(Controller controller) {
    this.controller = controller;
  }

  /**
  * Gửi request lên server.
  *
  * @param request request cần gửi
  */
  public synchronized void sendRequest(Request request) {
    if (out == null) {
      LOGGER.warn("ObjectOutputStream chưa được khởi tạo");
      return;
    }

    try {
      out.writeObject(request);
      out.flush();
      out.reset();
    } catch (IOException e) {
      LOGGER.error("Không thể gửi request {}", request.getClass().getSimpleName(), e);
    }
  }

  /**
  * Bật thread lắng nghe response từ server.
  */
  public synchronized void startListening() {
    if (listenerThread != null && listenerThread.isAlive()) {
      return;
    }
    if (in == null) {
      LOGGER.warn("ObjectInputStream chưa được khởi tạo");
      return;
    }

    listening = true;
    listenerThread = new Thread(
        () -> {
          while (listening) {
            try {
              Object obj = in.readObject();
              Platform.runLater(() -> controller.handle(obj));
            } catch (Exception e) {
              LOGGER.error("Lỗi khi lắng nghe response từ server", e);
              listening = false;
              break;
            }
          }
        }
    );
    listenerThread.setDaemon(true);
    listenerThread.start();
  }

  /**
  * Dừng thread lắng nghe.
  */
  public void stopListening() {
    listening = false;
  }

  /**
  * Lấy input stream.
  *
  * @return input stream
  */
  public ObjectInputStream getInStream() {
    return in;
  }

  /**
  * Lấy output stream.
  *
  * @return output stream
  */
  public ObjectOutputStream getOutStream() {
    return out;
  }

  /**
  * Lấy socket đang sử dụng.
  *
  * @return socket hiện tại
  */
  public Socket getSocket() {
    return socket;
  }
}
