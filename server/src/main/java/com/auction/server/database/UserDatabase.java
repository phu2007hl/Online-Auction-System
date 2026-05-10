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

public class UserDatabase {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserDatabase.class);
  private static ConcurrentHashMap<String, User> userData;
  private static String path;

  public static void saveUser(ConcurrentHashMap<String, User> userData) {
    try {
      ObjectOutputStream out =
      new ObjectOutputStream(
      new BufferedOutputStream(new FileOutputStream(path)));
      out.writeObject(userData);
      out.close();
      LOGGER.info("Đã lưu {} tài khoản", userData.size());
    } catch (Exception e) {
      LOGGER.error("Không thể lưu user", e);
    }
  }

  public static ConcurrentHashMap<String, User> loadUser() {
    try {
      ObjectInputStream in =
      new ObjectInputStream(
      new BufferedInputStream(new FileInputStream(path)));
      ConcurrentHashMap<String, User> loadedUserData =
      (ConcurrentHashMap<String, User>) in.readObject();
      in.close();
      return loadedUserData;
    } catch (Exception e) {
      LOGGER.error("Không thể tải user", e);
      return new ConcurrentHashMap<String, User>();
    }
  }

  public static ConcurrentHashMap<String, User> getUserData() {
    return userData;
  }

  public static void setUserData(ConcurrentHashMap<String, User> data) {
    userData = data;
  }

  public static void setPath(String pa) {
    path = pa;
  }
}
