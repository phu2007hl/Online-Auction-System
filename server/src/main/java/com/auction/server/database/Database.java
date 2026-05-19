package com.auction.server.database;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public abstract class Database<K> {
  private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);
  private K data;
  private String path;
  public abstract K createEmptyData();
  public Database(String pa){
    path = pa;
  }

  private K loadData(){
    File file = new File(path);
    if(!file.exists() || file.length()==0){
      return createEmptyData();
    }
    try {
      ObjectInputStream in =
      new ObjectInputStream(
      new BufferedInputStream(new FileInputStream(file)));
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
    File tempFile = new File(path + ".tmp");
    try {
      ObjectOutputStream out =
      new ObjectOutputStream(
      new BufferedOutputStream(new FileOutputStream(tempFile)));
      out.writeObject(dat);
      out.close();
      Files.move(
          tempFile.toPath(),
          new File(path).toPath(),
          StandardCopyOption.REPLACE_EXISTING);
      data = dat;
      LOGGER.info("Đã lưu dữ liệu");
    } catch (Exception e) {
      LOGGER.error("Không thể lưu dữ liệu", e);
      if (tempFile.exists() && !tempFile.delete()) {
        LOGGER.warn("Không thể xóa file tạm {}", tempFile.getName());
      }
    }
  }


  public  K getData() {
    if (data == null){
      data = loadData();
    }
    return data;
  } 
}
