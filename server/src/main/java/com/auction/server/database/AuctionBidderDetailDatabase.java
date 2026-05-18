package com.auction.server.database;

import java.util.concurrent.ConcurrentHashMap;

import com.auction.server.model.auction.AuctionBidderDetail;

public class AuctionBidderDetailDatabase extends Database<ConcurrentHashMap<Integer, AuctionBidderDetail>> {
  private static AuctionBidderDetailDatabase instance;
  private AuctionBidderDetailDatabase(){
    super("AuctionDetail.ser");
  }
  public static AuctionBidderDetailDatabase getInstance(){
    if (instance == null){
      instance = new AuctionBidderDetailDatabase();
    }
    return instance;
  }
  public ConcurrentHashMap<Integer, AuctionBidderDetail> createEmptyData(){
    return new ConcurrentHashMap<Integer, AuctionBidderDetail>();
  }


    
}
