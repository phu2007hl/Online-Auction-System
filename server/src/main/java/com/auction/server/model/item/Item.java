package com.auction.server.model.item;

import com.auction.shared.entity.Entity;
import com.auction.shared.enums.ItemCondition;
import com.auction.shared.model.User;

/**
* Lớp cơ sở cho các mặt hàng đấu giá.
*/
public abstract class Item extends Entity {
  protected String name;
  protected String description;
  protected ItemCondition condition;
  protected String imageUrl;
  protected User seller;

  /**
  * Tạo mặt hàng với các thuộc tính cơ bản.
  *
  * @param id mã mặt hàng
  * @param name tên mặt hàng
  * @param description mô tả mặt hàng
  * @param condition tình trạng mặt hàng
  * @param imageUrl đường dẫn ảnh
  * @param seller user bán hàng
  */
  public Item(
      int id,
      String name,
      String description,
      ItemCondition condition,
      String imageUrl,
      User seller) {
    super(id);
    this.name = name;
    this.description = description;
    this.condition = condition;
    this.imageUrl = imageUrl;
    this.seller = seller;
  }

  /**
  * In thông tin mặt hàng ra log.
  */
  public abstract void printInfo();

  /**
  * Kiểm tra mặt hàng có dữ liệu hợp lệ không.
  *
  * @return true nếu mặt hàng hợp lệ
  */
  public boolean validate() {
    return name != null
        && !name.trim().isEmpty()
        && seller != null;
  }

  /**
  * Lấy tên mặt hàng.
  *
  * @return tên mặt hàng
  */
  public String getName() {
    return name;
  }

  /**
  * Gán tên mặt hàng.
  *
  * @param name tên mặt hàng
  */
  public void setName(String name) {
    this.name = name;
  }

  /**
  * Lấy mô tả mặt hàng.
  *
  * @return mô tả mặt hàng
  */
  public String getDescription() {
    return description;
  }

  /**
  * Gán mô tả mặt hàng.
  *
  * @param description mô tả mặt hàng
  */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
  * Lấy tình trạng mặt hàng.
  *
  * @return tình trạng mặt hàng
  */
  public ItemCondition getCondition() {
    return condition;
  }

  /**
  * Gán tình trạng mặt hàng.
  *
  * @param condition tình trạng mặt hàng
  */
  public void setCondition(ItemCondition condition) {
    this.condition = condition;
  }

  /**
  * Lấy đường dẫn ảnh.
  *
  * @return đường dẫn ảnh
  */
  public String getImageUrl() {
    return imageUrl;
  }

  /**
  * Gán đường dẫn ảnh.
  *
  * @param imageUrl đường dẫn ảnh
  */
  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  /**
  * Lấy user bán hàng.
  *
  * @return user bán hàng
  */
  public User getSeller() {
    return seller;
  }

  /**
  * Gán user bán hàng.
  *
  * @param seller user bán hàng
  */
  public void setSeller(User seller) {
    this.seller = seller;
  }
}
