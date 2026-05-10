package com.auction.server.factory;

import com.auction.server.model.item.Art;
import com.auction.server.model.item.Electronics;
import com.auction.server.model.item.Item;
import com.auction.server.model.item.Vehicle;
import com.auction.shared.enums.ItemCondition;
import com.auction.shared.model.User;

/**
* Factory tạo các loại mặt hàng đấu giá khác nhau.
*/
public class ItemFactory {

  private static ItemFactory instance;

  private ItemFactory() {
  }

  /**
  * Lấy instance singleton của ItemFactory.
  *
  * @return instance ItemFactory
  */
  public static ItemFactory getInstance() {
    if (instance == null) {
      instance = new ItemFactory();
    }
    return instance;
  }

  /**
  * Tạo mặt hàng dựa trên loại được chỉ định.
  *
  * @param type loại mặt hàng
  * @param id mã mặt hàng
  * @param name tên mặt hàng
  * @param description mô tả mặt hàng
  * @param condition tình trạng mặt hàng
  * @param imageUrl đường dẫn ảnh
  * @param seller user bán hàng
  * @param extra1 tham số chuỗi bổ sung
  * @param extra2 tham số số bổ sung
  * @return mặt hàng đã tạo
  * @throws IllegalArgumentException nếu loại mặt hàng không được hỗ trợ
  */
  public Item createItem(
      String type,
      String id,
      String name,
      String description,
      ItemCondition condition,
      String imageUrl,
      User seller,
      String extra1,
      int extra2) {

    switch (type.toUpperCase()) {
      case "ELECTRONICS":
        return new Electronics(
            id,
            name,
            description,
            condition,
            imageUrl,
            seller,
            extra1,
            extra2);

      case "ART":
        return new Art(
            id,
            name,
            description,
            condition,
            imageUrl,
            seller,
            extra1,
            extra2);

      case "VEHICLE":
        return new Vehicle(
            id,
            name,
            description,
            condition,
            imageUrl,
            seller,
            extra1,
            extra2);

      default:
        throw new IllegalArgumentException("Loại mặt hàng không hợp lệ: " + type);
    }
  }
}
