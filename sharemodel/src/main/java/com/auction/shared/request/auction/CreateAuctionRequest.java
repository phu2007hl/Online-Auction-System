package com.auction.shared.request.auction;

import com.auction.shared.model.User;
import com.auction.shared.request.Request;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

/**
* Yêu cầu tạo auction do user gửi lên server.
*/
public class CreateAuctionRequest extends Request {
  private final byte[] imageContent;
  private final String category;
  private final double startingPrice;
  private final String description;
  private final LocalDate endDate;
  private final int id;
  private User user;
  private String itemName;
  private double minimumIncrement;


  /**
  * Tạo request tạo auction mới.
  *
  * @param imageContent noi dừng ảnh sản phẩm
  * @param category loại sản phẩm
  * @param startingPrice giá khởi điểm
  * @param description mô tả sản phẩm
  * @param endDate ngày kết thúc đấu giá
  */
  public CreateAuctionRequest(
      byte[] imageContent,
      String category,
      double startingPrice,
      String description,
      LocalDate endDate,
      int id,
      String name,
      double minimumIncrement) {
    this.imageContent = imageContent;
    this.category = category;
    this.startingPrice = startingPrice;
    this.description = description;
    this.endDate = endDate;
    this.id = id;
    this.itemName = name;
    this.minimumIncrement = minimumIncrement;
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
  * Lấy mô tả sản phẩm.
  *
  * @return mô tả sản phẩm
  */
  public String getDescription() {
    return description;
  }

  /**
  * Lấy ngày kết thúc đấu giá.
  *
  * @return ngày kết thúc
  */
  public LocalDate getEndDate() {
    return endDate;
  }

  /**
  * Gán user tạo auction.
  *
  * @param user user tạo auction
  */
  public void setUser(User user) {
    this.user = user;
  }

  /**
  * Lấy user tạo auction.
  *
  * @return user tạo auction
  */
  public User getUser() {
    return user;
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

}