package com.auction.server.database;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Database<K> {
  private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);
  private static final String DATA_PATH_PROPERTY = "dataPath";
  private static final String DEFAULT_DATA_PATH = "data";
  private K data;
  private final File dataFile;

  public abstract K createEmptyData();

  /**
   * Tạo database với file cụ thể.
   *
   * @param dataFile file để lưu/đọc dữ liệu
   */
  public Database(File dataFile) {
    this.dataFile = dataFile;
  }

  /**
   * Tạo database với tên file, mặc định lưu vào thư mục data/.
   *
   * @param filename tên file (vd: "User.ser")
   */
  public Database(String filename) {
    this.dataFile = new File(getDataDirectory(), filename);
  }

  private File getDataDirectory() {
    String dataPath = System.getProperty(DATA_PATH_PROPERTY);
    if (dataPath == null || dataPath.isBlank()) {
      dataPath = DEFAULT_DATA_PATH;
    }
    return new File(dataPath);
  }

  @SuppressWarnings("unchecked")
  private K loadData() {
    if (!dataFile.exists()) {
      LOGGER.info(
          "Chưa có file dữ liệu, dùng dữ liệu rỗng [path: {}]",
          dataFile.getAbsolutePath());
      return createEmptyData();
    }
    try (ObjectInputStream in =
          new ObjectInputStream(
              new BufferedInputStream(new FileInputStream(dataFile)))) {
      K loadedData = (K) in.readObject();
      return loadedData;
    } catch (Exception e) {
      LOGGER.error("Không thể tải dữ liệu [path: {}]", dataFile.getAbsolutePath(), e);
      return createEmptyData();
    }
  }

  public synchronized void saveData(K dataToSave) {
    try {
      File parentDirectory = dataFile.getParentFile();
      if (parentDirectory != null && !parentDirectory.exists()) {
        parentDirectory.mkdirs();
      }
      data = dataToSave;
      try (ObjectOutputStream out =
          new ObjectOutputStream(
              new BufferedOutputStream(new FileOutputStream(dataFile)))) {
        out.writeObject(data);
      }
      LOGGER.info("Đã lưu dữ liệu [path: {}]", dataFile.getAbsolutePath());
    } catch (Exception e) {
      LOGGER.error("Không thể lưu dữ liệu [path: {}]", dataFile.getAbsolutePath(), e);
    }
  }

  public synchronized K getData() {
    if (data == null) {
      data = loadData();
    }
    return data;
  }
}
