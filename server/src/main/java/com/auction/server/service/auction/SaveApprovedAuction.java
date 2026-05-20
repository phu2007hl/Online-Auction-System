package com.auction.server.service.auction;

import java.util.concurrent.ConcurrentHashMap;

import com.auction.server.database.AuctionListDatabase;
import com.auction.shared.auction.Auction;

public class SaveApprovedAuction {
  public static void saveToDatabase(Auction auction){
    AuctionListDatabase database = AuctionListDatabase.getInstance();
    ConcurrentHashMap<Integer,Auction> auctionList = database.getData();
    auctionList.put(auction.getId(),auction);
    database.saveData(auctionList);

  }
}
