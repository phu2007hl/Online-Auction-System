package com.auction.server.database;

import com.auction.shared.model.User;
import com.auction.shared.request.Request;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminResponseDatabase {
  private static final Logger LOGGER = LoggerFactory.getLogger(AdminResponseDatabase.class);

  public static void saveAdminResponse(HashMap<User, Request> adminResponse) {
    try {
      ObjectOutputStream out =
      new ObjectOutputStream(
      new BufferedOutputStream(new FileOutputStream("AdminResponse.ser")));
      out.writeObject(adminResponse);
      out.flush();
      LOGGER.info("Đã lưu {} phản hồi admin", adminResponse.size());
    } catch (Exception e) {
      LOGGER.error("Không thể lưu phản hồi admin", e);
    }
  }

  public static HashMap<User, Request> loadAdminResponse() {
    File file = new File("AdminResponse.ser");
    if (!file.exists() || file.length() == 0) {
      return new HashMap<User, Request>();
    }

    try {
      ObjectInputStream in =
      new ObjectInputStream(
      new BufferedInputStream(new FileInputStream("AdminResponse.ser")));
      HashMap<User, Request> adminResponse = (HashMap<User, Request>) in.readObject();
      in.close();
      LOGGER.info("Đã tải {} phản hồi admin", adminResponse.size());
      return adminResponse;
    } catch (ClassNotFoundException e) {
      LOGGER.warn("File AdminResponse.ser không tương thích với package hiện tại, reset dữ liệu");
      HashMap<User, Request> emptyMap = new HashMap<User, Request>();
      saveAdminResponse(emptyMap);
      return emptyMap;
    } catch (IOException e) {
      LOGGER.warn("Không thể đọc file AdminResponse.ser, reset dữ liệu");
      HashMap<User, Request> emptyMap = new HashMap<User, Request>();
      saveAdminResponse(emptyMap);
      return emptyMap;
    } catch (Exception e) {
      LOGGER.error("Có lỗi khi tải phản hồi admin", e);
      return new HashMap<User, Request>();
    }
  }
}
