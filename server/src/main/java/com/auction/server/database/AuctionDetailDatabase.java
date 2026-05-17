package com.auction.server.database;

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
import com.auction.server.network.ClientHandler;
import com.auction.shared.auction.Auction;

public class AuctionDetailDatabase extends Database<ConcurrentHashMap<Integer,ArrayList<ClientHandler>>> {
  private static AuctionDetailDatabase instance;
  private AuctionDetailDatabase(){
    super("AuctionDetail.ser");
  }
  public static AuctionDetailDatabase getInstance(){
    if (instance == null){
      instance = new AuctionDetailDatabase();
    }
    return instance;
  }
  public ConcurrentHashMap<Integer,ArrayList<ClientHandler>> createEmptyData(){
    return new ConcurrentHashMap<Integer,ArrayList<ClientHandler>>();
  }


    
}
