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

public class AuctionListDatabase {
  private static final Logger LOGGER = LoggerFactory.getLogger(AuctionListDatabase.class);
  private static String path;
  private static ConcurrentHashMap<Integer,Auction> auctionList;

  public static void saveAuction(ConcurrentHashMap<Integer,Auction> auctionList) {
    try {
      ObjectOutputStream out =
      new ObjectOutputStream(
      new BufferedOutputStream(new FileOutputStream(path)));
      out.writeObject(auctionList);
      out.flush();
      LOGGER.info("Đã lưu {} auction đã duyệt", auctionList.size());
    } catch (Exception e) {
      LOGGER.error("Không thể lưu danh sách auction", e);
    }
  }

  public static ConcurrentHashMap<Integer,Auction> loadAuctionList() {
    if (path == null || !new File(path).exists() || new File(path).length() == 0) {
      return new ConcurrentHashMap<Integer,Auction>();
    }

    try {
      ObjectInputStream in =
      new ObjectInputStream(
      new BufferedInputStream(new FileInputStream(path)));
      ConcurrentHashMap<Integer,Auction> auctionList = (ConcurrentHashMap<Integer,Auction>) in.readObject();
      in.close();
      LOGGER.info("Đã tải {} auction đã duyệt", auctionList.size());
      return auctionList;
    } catch (ClassNotFoundException e) {
      LOGGER.warn("File {} không tương thích với package hiện tại, reset danh sách auction", path);
      ConcurrentHashMap<Integer,Auction> emptyList = new ConcurrentHashMap<Integer,Auction>();
      saveAuction(emptyList);
      return emptyList;
    } catch (IOException e) {
      LOGGER.warn("Không thể đọc file {}, reset danh sách auction", path);
      ConcurrentHashMap<Integer,Auction> emptyList = new ConcurrentHashMap<Integer,Auction>();
      saveAuction(emptyList);
      return emptyList;
    } catch (Exception e) {
      LOGGER.error("Không thể tải danh sách auction", e);
      return new ConcurrentHashMap<Integer,Auction>();
    }
  }

  public static void setPath(String pa) {
    path = pa;
  }
  public static void setAuctionList(ConcurrentHashMap<Integer,Auction> list){
    auctionList = list;
  }
  public static ConcurrentHashMap<Integer,Auction> getAuctionList(){
    return auctionList;
  }
}
