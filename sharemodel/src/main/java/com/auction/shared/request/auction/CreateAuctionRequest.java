package com.auction.shared.request.auction;

import com.auction.shared.request.Request;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
* Yêu cầu tạo auction do user gửi lên server.
*/
public class CreateAuctionRequest extends Request {
  private byte[] imageContent;
  private String category;
  private final double startingPrice;
  private String description;
  private LocalDateTime endTime;
  private final int id;
  private String itemName;
  private final double minimumIncrement;
  private boolean antiSnippingEnabled;


  /**
  * Tạo request tạo auction mới.
  *
  * @param imageContent noi dừng ảnh sản phẩm
  * @param category loại sản phẩm
  * @param startingPrice giá khởi điểm
  * @param description mô tả sản phẩm
  * @param endTime thời điểm kết thúc đấu giá
  */
  public CreateAuctionRequest(
      byte[] imageContent,
      String category,
      double startingPrice,
      String description,
      LocalDateTime endTime,
      int id,
      String name,
      double minimumIncrement) {
    this(
        imageContent,
        category,
        startingPrice,
        description,
        endTime,
        id,
        name,
        minimumIncrement,
        false);
  }

  public CreateAuctionRequest(
      byte[] imageContent,
      String category,
      double startingPrice,
      String description,
      LocalDateTime endTime,
      int id,
      String name,
      double minimumIncrement,
      boolean antiSnippingEnabled) {
    this.imageContent = imageContent;
    this.category = category;
    this.startingPrice = startingPrice;
    this.description = description;
    this.endTime = endTime;
    this.id = id;
    this.itemName = name;
    this.minimumIncrement = minimumIncrement;
    this.antiSnippingEnabled = antiSnippingEnabled;
  }

  /**
  * Lấy noi dừng ảnh sản phẩm.
  *
  * @return dữ liệu ảnh
  */
  public byte[] getImageContent() {
    return imageContent;
  }

  /**
  * Lấy loại sản phẩm.
  *
  * @return loại sản phẩm
  */
  public String getCategory() {
    return category;
  }

  /**
  * Lấy giá khởi điểm.
  *
  * @return giá khởi điểm
  */
  public double getStartingPrice() {
    return startingPrice;
  }

  /**
  * Lấy bước giá tối thiểu.
  *
  * @return bước giá tối thiểu
  */
  public double getMinimumIncrement() {
    return minimumIncrement;
  }

  public boolean isAntiSnippingEnabled() {
    return antiSnippingEnabled;
  }

  /**
  * Lấy mô tả sản phẩm.
  *
  * @return mô tả sản phẩm
  */
  public String getDescription() {
    return description;
  }

  /**
  * Lấy thời điểm kết thúc đấu giá.
  *
  * @return thời điểm kết thúc
  */
  public LocalDateTime getEndTime() {
    return endTime;
  }

  /**
  * Lấy riêng ngày kết thúc đấu giá.
  *
  * @return ngày kết thúc
  */
  public LocalDate getEndDate() {
    return endTime.toLocalDate();
  }

  /**
   * Lấy id của Request tạo auction này
   * @return ID request
   */
  public int getId(){
    return id;
  }
  /**
   * Lấy tên sản phẩm
   * @return tên sản phẩm
   */
  public String getName(){
    return itemName;
  }

  public void setImageContent(byte[] imageContent) {
    this.imageContent = imageContent;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setItemName(String itemName) {
    this.itemName = itemName;
  }

  public void setEndTime(LocalDateTime endTime) {
    this.endTime = endTime;
  }

  public void setAntiSnippingEnabled(boolean antiSnippingEnabled) {
    this.antiSnippingEnabled = antiSnippingEnabled;
  }
}
