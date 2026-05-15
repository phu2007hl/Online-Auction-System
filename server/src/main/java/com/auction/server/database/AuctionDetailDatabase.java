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

public class AuctionDetailDatabase {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuctionListDatabase.class);
    private static ConcurrentHashMap<Integer,ArrayList<ClientHandler>> auctionParticipant;
    private static String path;
    public static void setPath(String pa){
        path = pa;
    }
    public static ConcurrentHashMap<Integer,ArrayList<ClientHandler>>  getAuctionParticipant(){
        return auctionParticipant;
    }
    public static void setAuctionPartcipant(ConcurrentHashMap<Integer,ArrayList<ClientHandler>> participant){

        auctionParticipant = participant;
    }
  public static void saveData(ConcurrentHashMap<Integer,ArrayList<ClientHandler>> auctionParticipant) {
    try {
      ObjectOutputStream out =
      new ObjectOutputStream(
      new BufferedOutputStream(new FileOutputStream(path)));
      out.writeObject(auctionParticipant);
      out.flush();
      LOGGER.info("Đã lưu {} auction đã duyệt", auctionParticipant.size());
    } catch (Exception e) {
      LOGGER.error("Không thể lưu danh sách auction", e);
    }
  }
  public static ConcurrentHashMap<Integer,ArrayList<ClientHandler>> loadData(){
    if (path == null || !new File(path).exists() || new File(path).length() == 0){
        return new ConcurrentHashMap<Integer,ArrayList<ClientHandler>>();
    }
    try{
        ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(path)));
        ConcurrentHashMap<Integer,ArrayList<ClientHandler>> auctionParticipant = (ConcurrentHashMap<Integer,ArrayList<ClientHandler>>) in.readObject();
        in.close();
        LOGGER.info("Đã tải {} auction đã duyệt", auctionParticipant.size());
        return auctionParticipant;
    }
    catch (ClassNotFoundException e){
      LOGGER.warn("File {} không tương thích với package hiện tại, reset danh sách auction", path);
      ConcurrentHashMap<Integer,ArrayList<ClientHandler>> emptyList = new ConcurrentHashMap<Integer,ArrayList<ClientHandler>>();
      saveData(emptyList);
      return emptyList;



    }
    catch (IOException e){
      LOGGER.warn("Không thể đọc file {}, reset danh sách auction", path);
      ConcurrentHashMap<Integer,ArrayList<ClientHandler>> emptyList = new ConcurrentHashMap<Integer,ArrayList<ClientHandler>>();
      saveData(emptyList);
      return emptyList;
    }
    catch (Exception e){
      LOGGER.error("Không thể tải danh sách auction", e);
      return new ConcurrentHashMap<Integer,ArrayList<ClientHandler>>();        
    }

  }



    
}
