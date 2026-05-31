package com.auction.shared.auction;

import java.io.Serializable;
import java.time.LocalDateTime;

public class BidTransaction implements Serializable {

  private String bidderUsername;
  private double bidAmount;
  private LocalDateTime bidTime;

  public BidTransaction(String bidderUsername, double bidAmount) {
    this(bidderUsername, bidAmount, LocalDateTime.now());
  }

  public BidTransaction(String bidderUsername, double bidAmount, LocalDateTime bidTime) {
    this.bidderUsername = bidderUsername;
    this.bidAmount = bidAmount;
    this.bidTime = bidTime;
  }

  public String getBidderUsername() {
    return bidderUsername;
  }

  public double getBidAmount() {
    return bidAmount;
  }

  public LocalDateTime getBidTime() {
    return bidTime;
  }
}
