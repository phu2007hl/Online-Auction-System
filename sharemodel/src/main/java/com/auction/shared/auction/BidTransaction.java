package com.auction.shared.auction;

import com.auction.shared.entity.Entity;
import com.auction.shared.model.User;

public class BidTransaction extends Entity {

  private User bidder;
  private double bidAmount;
  private int auctionId;

  public BidTransaction(int id, User bidder, double bidAmount, int auctionId) {
    super(id);
    this.bidder = bidder;
    this.bidAmount = bidAmount;
    this.auctionId = auctionId;
  }

  public User getBidder() {
    return bidder;
  }

  public double getBidAmount() {
    return bidAmount;
  }

  public int getAuctionId() {
    return auctionId;
  }


}
