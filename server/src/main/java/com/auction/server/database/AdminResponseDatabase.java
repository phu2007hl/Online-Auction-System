package com.auction.server.database;

import com.auction.shared.request.auction.PendingAuctionReviewRequest;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminResponseDatabase extends Database<LinkedHashMap<Integer, PendingAuctionReviewRequest>> {
  private static AdminResponseDatabase instance;
  private AdminResponseDatabase(){
    super("AdminResponse.ser");
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
