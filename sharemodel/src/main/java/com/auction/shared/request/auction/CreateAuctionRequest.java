package com.auction.shared.request.auction;

import com.auction.shared.model.User;
import com.auction.shared.request.Request;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

/**
* Yêu cầu tạo auction do user gửi lên server.
*/
public class CreateAuctionRequest extends Request {
  private final byte[] imageContent;
  private final String category;
  private final String startingPrice;
  private final String description;
  private final LocalDate endDate;
  private User user;

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
      String startingPrice,
      String description,
      LocalDate endDate) {
    this.imageContent = imageContent;
    this.category = category;
    this.startingPrice = startingPrice;
    this.description = description;
    this.endDate = endDate;
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
  public String getStartingPrice() {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CreateAuctionRequest)) {
      return false;
    }

    CreateAuctionRequest request = (CreateAuctionRequest) o;
    return Arrays.equals(imageContent, request.getImageContent())
      && Objects.equals(category, request.getCategory())
      && Objects.equals(startingPrice, request.getStartingPrice())
      && Objects.equals(description, request.getDescription())
      && Objects.equals(endDate, request.getEndDate());
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(category, startingPrice, description, endDate);
    result = 31 * result + Arrays.hashCode(imageContent);
    return result;
  }
}
