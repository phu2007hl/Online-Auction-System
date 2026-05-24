package com.auction.server.network;

import com.auction.server.database.AdminResponseDatabase;
import com.auction.server.database.AuctionBidderDetailDatabase;
import com.auction.server.database.AuctionListDatabase;
import com.auction.server.database.PendingAuctionDatabase;
import com.auction.server.database.UserDatabase;
import com.auction.server.service.auction.AuctionClosingScheduler;
import java.net.ServerSocket;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Điểm khởi động của server đấu giá.
*/
public class AuctionServer {
  private static final Logger LOGGER = LoggerFactory.getLogger(AuctionServer.class);

  /**
  * Khởi động server và lắng nghe kết nối mới.
  *
  * @param args tham số dòng lệnh
  */
  public static void main(String[] args) {
    try {
      AdminResponseDatabase adminResponseDatabase = AdminResponseDatabase.getInstance();
      AuctionBidderDetailDatabase auctionDetailDatabase = AuctionBidderDetailDatabase.getInstance();
      AuctionListDatabase auctionListDatabase = AuctionListDatabase.getInstance();
      PendingAuctionDatabase pendingAuctionDatabase = PendingAuctionDatabase.getInstance();
      UserDatabase userDatabase = UserDatabase.getInstance();
      AuctionClosingScheduler.start();

      ServerSocket server = new ServerSocket(4100);
      LOGGER.info("Server đã khởi động ở cổng 4100");

      while (true) {
        Socket connection = server.accept();
        LOGGER.info("Client đã kết nối: {}", connection.getInetAddress());

        ClientHandler job = new ClientHandler(connection);
        Thread thread = new Thread(job);
        thread.start();
      }
    } catch (Exception e) {
      LOGGER.error("Không thể khởi động server", e);
    }
  }
}
