package com.auction.server.database;

import com.auction.shared.model.User;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

public class UserDatabase extends Database<ConcurrentHashMap<String, User>>{
  private static UserDatabase instance;
  private UserDatabase(){
    super("User.ser");
  }
  public UserDatabase(File dataFile){
    super(dataFile);
  }
  public static UserDatabase getInstance(){
    if (instance == null){
      instance = new UserDatabase();

    }
    return instance;
  }
  public ConcurrentHashMap<String, User> createEmptyData(){
    return new ConcurrentHashMap<String,User>();
  }
}
