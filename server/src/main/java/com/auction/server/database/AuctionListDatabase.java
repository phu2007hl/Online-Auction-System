package com.auction.server.database;

import com.auction.shared.auction.Auction;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

public class AuctionListDatabase extends Database<ConcurrentHashMap<Integer,Auction>> {
  private static AuctionListDatabase instance;
  private AuctionListDatabase(){
    super("AuctionList.ser");
  }
  public AuctionListDatabase(File dataFile){
    super(dataFile);
  }
  public static AuctionListDatabase getInstance(){
    if (instance == null){
      instance = new AuctionListDatabase();
    }
    return instance;
  }
  public ConcurrentHashMap<Integer,Auction> createEmptyData(){
    return new ConcurrentHashMap<Integer,Auction>();
  }

}
