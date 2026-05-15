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

public class UserBidStatusDatabase {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuctionListDatabase.class);
    private static ConcurrentHashMap<Integer,HashMap<User,ParticipantStatus>> bidderStatus;
    private static String path;
    public static void setPath(String pa){
        path = pa;
    }
    public static ConcurrentHashMap<Integer,HashMap<User,ParticipantStatus>>  getBidderStatus(){
        return bidderStatus;
    }
    public static void setBidderStatus(ConcurrentHashMap<Integer,HashMap<User,ParticipantStatus>> status){

        bidderStatus = status;
    }
  public static void saveData(ConcurrentHashMap<Integer,HashMap<User,ParticipantStatus>> bidderStatus) {
    try {
      ObjectOutputStream out =
      new ObjectOutputStream(
      new BufferedOutputStream(new FileOutputStream(path)));
      out.writeObject(bidderStatus);
      out.flush();
      LOGGER.info("Đã lưu {} auction đã duyệt", bidderStatus.size());
    } catch (Exception e) {
      LOGGER.error("Không thể lưu danh sách auction", e);
    }
  }
  public static ConcurrentHashMap<Integer,HashMap<User,ParticipantStatus>> loadData(){
    if (path == null || !new File(path).exists() || new File(path).length() == 0){
        return new ConcurrentHashMap<Integer,HashMap<User,ParticipantStatus>>();
    }
    try{
        ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(path)));
        ConcurrentHashMap<Integer,HashMap<User,ParticipantStatus>> bidderStatus = (ConcurrentHashMap<Integer,HashMap<User,ParticipantStatus>>) in.readObject();
        in.close();
        LOGGER.info("Đã tải {} auction đã duyệt", bidderStatus.size());
        return bidderStatus;
    }
    catch (ClassNotFoundException e){
      LOGGER.warn("File {} không tương thích với package hiện tại, reset danh sách auction", path);
      ConcurrentHashMap<Integer,HashMap<User,ParticipantStatus>> emptyList = new ConcurrentHashMap<Integer,HashMap<User,ParticipantStatus>>();
      saveData(emptyList);
      return emptyList;



    }
    catch (IOException e){
      LOGGER.warn("Không thể đọc file {}, reset danh sách auction", path);
      ConcurrentHashMap<Integer,HashMap<User,ParticipantStatus>> emptyList = new ConcurrentHashMap<Integer,HashMap<User,ParticipantStatus>>();
      saveData(emptyList);
      return emptyList;
    }
    catch (Exception e){
      LOGGER.error("Không thể tải danh sách auction", e);
      return new ConcurrentHashMap<Integer,HashMap<User,ParticipantStatus>>();        
    }

  }



    
}
