package com.auction.server.database;

import com.auction.shared.request.auction.PendingAuctionReviewRequest;
import java.io.File;
import java.util.LinkedHashMap;

public class AdminResponseDatabase extends Database<LinkedHashMap<Integer, PendingAuctionReviewRequest>> {
  private static AdminResponseDatabase instance;
  private AdminResponseDatabase(){
    super("AdminResponse.ser");
  }
  public AdminResponseDatabase(File dataFile){
    super(dataFile);
  }
  public static AdminResponseDatabase getInstance(){
    if (instance == null){
      instance = new AdminResponseDatabase();
    }
    return instance;
  }
  public LinkedHashMap<Integer, PendingAuctionReviewRequest> createEmptyData(){
    return new LinkedHashMap<Integer, PendingAuctionReviewRequest>();
  }
}
