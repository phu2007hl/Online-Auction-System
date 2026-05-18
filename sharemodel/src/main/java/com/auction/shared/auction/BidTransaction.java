package com.auction.shared.auction;

import com.auction.shared.model.User;

public class BidTransaction{

  private String bidderUsername;
  private double bidAmount;

  public BidTransaction(String bidderUsername, double bidAmount) {
    this.bidderUsername = bidderUsername;
    this.bidAmount = bidAmount;
  }

  public String getBidderUsername() {
    return bidderUsername;
  }

  public double getBidAmount() {
    return bidAmount;
  }

}
