package com.auction.server.factory;

import com.auction.server.model.item.Art;
import com.auction.server.model.item.Electronics;
import com.auction.server.model.item.Item;
import com.auction.server.model.item.Vehicle;
import com.auction.shared.enums.ItemCondition;
import com.auction.shared.model.User;

public class ItemFactory {

    private static ItemFactory instance;

    private ItemFactory() {
    }

    public static ItemFactory getInstance() {
        if (instance == null) {
            instance = new ItemFactory();
        }
        return instance;
    }

    public Item createItem(String type, String id, String name, String description,
                           ItemCondition condition, String imageUrl, User seller,
                           String extra1, int extra2) {

        switch (type.toUpperCase()) {
            case "ELECTRONICS":
                return new Electronics(id, name, description, condition, imageUrl, seller, extra1, extra2);

            case "ART":
                return new Art(id, name, description, condition, imageUrl, seller, extra1, extra2);

            case "VEHICLE":
                return new Vehicle(id, name, description, condition, imageUrl, seller, extra1, extra2);

            default:
                throw new IllegalArgumentException("Loai mat hang khong hop le: " + type);
        }
    }
}
