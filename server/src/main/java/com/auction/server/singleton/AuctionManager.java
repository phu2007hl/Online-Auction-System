package com.auction.server.singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Singleton quản lý các thao tác liên quan tới auction theo cách an toàn luồng.
*/
public class AuctionManager {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(AuctionManager.class);
  private static volatile AuctionManager instance;

  private AuctionManager() {
  }

  /**
  * Lấy instance singleton của AuctionManager.
  *
  * @return instance AuctionManager
  */
  public static AuctionManager getInstance() {
    if (instance == null) {
      synchronized (AuctionManager.class) {
        if (instance == null) {
          instance = new AuctionManager();
          LOGGER.debug("Đã tạo instance AuctionManager");
        }
      }
    }
    return instance;
  }
}
