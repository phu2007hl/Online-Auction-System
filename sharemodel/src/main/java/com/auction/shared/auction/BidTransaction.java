package com.auction.shared.auction;

import java.io.Serializable;

public class BidTransaction implements Serializable {
  private static final long serialVersionUID = 1L;

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
