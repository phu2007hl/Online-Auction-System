package com.auction.server.database;

import com.auction.shared.auction.Auction;
import com.auction.shared.request.Request;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PendingAuctionDatabase extends Database<ConcurrentHashMap<Integer,Request>> {
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
  public ConcurrentHashMap<Integer,Request> createEmptyData(){
    return new ConcurrentHashMap<Integer,Request>();
  }


}
