package com.auction.server.database;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.LoggerFactory;

import com.auction.shared.model.User;

import ch.qos.logback.classic.Logger;

public abstract class Database<K> {
  private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(Database.class);
  private K data;
  private String path;
  public abstract K createEmptyData();
  public Database(String pa){
    path = pa;
  }

  private K loadData(){
    try {
      ObjectInputStream in =
      new ObjectInputStream(
      new BufferedInputStream(new FileInputStream(path)));
      K loadedUserData =
      (K) in.readObject();
      in.close();
      return loadedUserData;
    } catch (Exception e) {
      LOGGER.error("Không thể tải dữ liệu", e);
      return createEmptyData();
    }
  }
  public void saveData(K dat){
    try {

      data = dat;
      ObjectOutputStream out =
      new ObjectOutputStream(
      new BufferedOutputStream(new FileOutputStream(path)));
      out.writeObject(data);
      out.close();
      LOGGER.info("Đã lưu dữ liệu");
    } catch (Exception e) {
      LOGGER.error("Không thể lưu dữ liệu", e);
    }
  }


  public  K getData() {
    if (data == null){
      data = loadData();
    }
    return data;
  } 
}
