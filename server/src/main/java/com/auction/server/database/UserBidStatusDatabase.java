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
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.auction.server.network.ClientHandler;
import com.auction.shared.auction.Auction;
import com.auction.shared.enums.ParticipantStatus;
import com.auction.shared.model.User;

public class UserBidStatusDatabase extends Database<ConcurrentHashMap<Integer,HashMap<User,ParticipantStatus>>> {
  private static UserBidStatusDatabase instance;
  private UserBidStatusDatabase(){
    super("UserBidStatus.ser");
  }
  public static UserBidStatusDatabase getInstance(){
    if (instance == null){
      instance = new UserBidStatusDatabase();
    }
    return instance;
  }
  public ConcurrentHashMap<Integer,HashMap<User,ParticipantStatus>> createEmptyData(){
    return new ConcurrentHashMap<Integer,HashMap<User,ParticipantStatus>>();
  }

}
