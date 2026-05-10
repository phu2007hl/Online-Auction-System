package com.auction.server.model.item;

import com.auction.shared.enums.ItemCondition;
import com.auction.shared.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vehicle extends Item {
  private static final Logger LOGGER = LoggerFactory.getLogger(Vehicle.class);
  private String brand;
  private int year;

  public Vehicle(String id, String name, String description, ItemCondition condition,
  String imageUrl, User seller, String brand, int year) {
    super(id, name, description, condition, imageUrl, seller);
    this.brand = brand;
    this.year = year;
  }

  @Override
  public void printInfo() {
    LOGGER.info(
    "[Phương tiện] {} | Thương hiệu: {} | Năm: {} | Tình trạng: {}",
    name,
    brand,
    year,
    condition);
  }

  public String getBrand() { return brand; }
  public void setBrand(String brand) { this.brand = brand; }

  public int getYear() { return year; }
  public void setYear(int year) { this.year = year; }
}
