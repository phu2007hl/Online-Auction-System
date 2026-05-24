package com.auction.server.database;

import java.util.concurrent.ConcurrentHashMap;

import com.auction.shared.request.auction.PendingAuctionReviewRequest;

public class PendingAuctionDatabase extends Database<ConcurrentHashMap<Integer, PendingAuctionReviewRequest>> {
  private static PendingAuctionDatabase instance;
  private PendingAuctionDatabase(){
    super("PendingAuction.ser");
  }

  public static PendingAuctionDatabase getInstance(){
    if (instance == null){
      instance = new PendingAuctionDatabase();
    }
    return instance;
  }
  public ConcurrentHashMap<Integer, PendingAuctionReviewRequest> createEmptyData(){
    return new ConcurrentHashMap<Integer,PendingAuctionReviewRequest>();
  }


}
