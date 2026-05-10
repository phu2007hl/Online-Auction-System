package com.auction.server.database;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PendingAuctionDatabase {
  private static final Logger LOGGER = LoggerFactory.getLogger(PendingAuctionDatabase.class);
  private static String path;

  public static void saveAuctionRequest(ArrayList<Request> requestList) {
    try {
      ObjectOutputStream out =
      new ObjectOutputStream(
      new BufferedOutputStream(new FileOutputStream(path)));
      out.writeObject(requestList);
      out.flush();
      LOGGER.info("Đã lưu vào danh sách request chờ duyệt, số request hiện có: {}", requestList.size());
    } catch (Exception e) {
      LOGGER.error("Không thể lưu danh sách request chờ duyệt", e);
    }
  }

  public static ArrayList<Request> loadRequestList() {
    if (path == null || !new File(path).exists() || new File(path).length() == 0) {
      return new ArrayList<Request>();
    }

    try {
      ObjectInputStream in =
      new ObjectInputStream(
      new BufferedInputStream(new FileInputStream(path)));
      ArrayList<Request> requestList = (ArrayList<Request>) in.readObject();
      in.close();
      LOGGER.info("Đã tải danh sách request chờ duyệt, số request hiện có: {}", requestList.size());
      return requestList;
    } catch (ClassNotFoundException e) {
      LOGGER.warn("File {} không tương thích với package hiện tại, reset danh sách chờ duyệt", path);
      ArrayList<Request> emptyList = new ArrayList<Request>();
      saveAuctionRequest(emptyList);
      return emptyList;
    } catch (IOException e) {
      LOGGER.warn("Không thể đọc file {}, reset danh sách chờ duyệt", path);
      ArrayList<Request> emptyList = new ArrayList<Request>();
      saveAuctionRequest(emptyList);
      return emptyList;
    } catch (Exception e) {
      LOGGER.error("Không thể tải danh sách request chờ duyệt", e);
      return new ArrayList<Request>();
    }
  }

  public static void removeRequest(Request request) {
    ArrayList<Request> requestList = PendingAuctionDatabase.loadRequestList();
    requestList.remove(request);
    LOGGER.info("Đã xóa request khỏi danh sách chờ duyệt");
    PendingAuctionDatabase.saveAuctionRequest(requestList);
  }

  public static void setPath(String pa) {
    path = pa;
  }
}
