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

public class AuctionListDatabase extends Database<ConcurrentHashMap<Integer,Auction>> {
  private static AuctionListDatabase instance;
  private AuctionListDatabase(){
    super("AuctionList.ser");
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
