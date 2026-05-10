package com.auction.shared.auction;

import com.auction.shared.entity.Entity;
import com.auction.shared.enums.AuctionStatus;
import com.auction.shared.model.User;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
* Thông tin một phiên đấu giá.
*/
public class Auction extends Entity {
  private String itemName;
  private String description;
  private User seller;

  private double startingPrice;
  private double currentPrice;
  private double minimumIncrement;

  private LocalDateTime startTime;
  private LocalDateTime endTime;

  private AuctionStatus status;
  private User winner;

  private ArrayList<BidTransaction> bidHistory;

  /**
  * Tạo phiên đấu giá mới.
  *
  * @param id mã phiên đấu giá
  * @param itemName tên sản phẩm
  * @param description mô tả sản phẩm
  * @param seller người bán
  * @param startingPrice giá khởi điểm
  * @param minimumIncrement bước giá tối thiểu
  * @param endTime thời điểm kết thúc
  */
  public Auction(
      String id,
      String itemName,
      String description,
      User seller,
      double startingPrice,
      double minimumIncrement,
      LocalDateTime endTime) {
    super(id);
    this.itemName = itemName;
    this.description = description;
    this.seller = seller;
    this.startingPrice = startingPrice;
    this.currentPrice = startingPrice;
    this.minimumIncrement = minimumIncrement;
    this.endTime = endTime;
    this.status = AuctionStatus.OPEN;
    this.winner = null;
    this.bidHistory = new ArrayList<>();
  }
}
