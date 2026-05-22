package com.auction.server.database;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public abstract class Database<K> {
  private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);
  private K data;
  private final File file;
  public abstract K createEmptyData();
  public Database(String pa){
    file = resolveDataFile(pa);
  }

  private File resolveDataFile(String path) {
    File configuredFile = new File(path);
    if (configuredFile.isAbsolute()) {
      return configuredFile;
    }

    File workingDirectory = new File(System.getProperty("user.dir"));
    String directoryName = workingDirectory.getName();
    if ("client".equals(directoryName) || "server".equals(directoryName)) {
      return new File(workingDirectory.getParentFile(), path);
    }
    return new File(workingDirectory, path);
  }

  private K loadData(){
    if(!file.exists() || file.length()==0){
      LOGGER.info("Tạo dữ liệu rỗng vì file không tồn tại hoặc rỗng [path: {}]", file.getAbsolutePath());
      return createEmptyData();
    }
    try {
      ObjectInputStream in =
      new ObjectInputStream(
      new BufferedInputStream(new FileInputStream(file)));
      K loadedUserData =
      (K) in.readObject();
      in.close();
      LOGGER.info("Đã tải dữ liệu [path: {}]", file.getAbsolutePath());
      return loadedUserData;
    } catch (Exception e) {
      LOGGER.error("Không thể tải dữ liệu [path: {}]", file.getAbsolutePath(), e);
      return createEmptyData();
    }
  }
  public void saveData(K dat){
    File tempFile = new File(file.getAbsolutePath() + ".tmp");
    try {
      ObjectOutputStream out =
      new ObjectOutputStream(
      new BufferedOutputStream(new FileOutputStream(tempFile)));
      out.writeObject(dat);
      out.close();
      Files.move(
          tempFile.toPath(),
          file.toPath(),
          StandardCopyOption.REPLACE_EXISTING);
      data = dat;
      LOGGER.info("Đã lưu dữ liệu [path: {}]", file.getAbsolutePath());
    } catch (Exception e) {
      LOGGER.error("Không thể lưu dữ liệu [path: {}]", file.getAbsolutePath(), e);
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
