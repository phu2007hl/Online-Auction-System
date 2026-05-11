package com.auction.server.model.item;

import com.auction.shared.enums.ItemCondition;
import com.auction.shared.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Electronics extends Item {
  private static final Logger LOGGER = LoggerFactory.getLogger(Electronics.class);
  private String brand;
  private int warranty;

  public Electronics(int id, String name, String description, ItemCondition condition,
  String imageUrl, User seller, String brand, int warranty) {
    super(id, name, description, condition, imageUrl, seller);
    this.brand = brand;
    this.warranty = warranty;
  }

  @Override
  public void printInfo() {
    LOGGER.info(
    "[Đồ điện tử] {} | Thương hiệu: {} | Bảo hành: {} tháng | Tình trạng: {}",
    name,
    brand,
    warranty,
    condition);
  }

  public String getBrand() { return brand; }
  public void setBrand(String brand) { this.brand = brand; }

  public int getWarranty() { return warranty; }
  public void setWarranty(int warranty) { this.warranty = warranty; }
}
