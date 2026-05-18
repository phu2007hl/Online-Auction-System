package com.auction.server.database;

import com.auction.shared.model.User;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDatabase extends Database<ConcurrentHashMap<String, User>>{
  private static UserDatabase instance;
  private UserDatabase(){
    super("User.ser");
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

