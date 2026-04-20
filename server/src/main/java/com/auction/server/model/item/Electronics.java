package com.auction.server.model.item;

import com.auction.shared.enums.ItemCondition;
import com.auction.shared.model.User;

public class Electronics extends Item {
    private String brand;
    private int warranty;

    public Electronics(String id, String name, String description, ItemCondition condition,
                       String imageUrl, User seller, String brand, int warranty) {
        super(id, name, description, condition, imageUrl, seller);
        this.brand = brand;
        this.warranty = warranty;
    }

    @Override
    public void printInfo() {
        System.out.println("[Electronics] " + name + " | Brand: " + brand +
                " | Warranty: " + warranty + " months | Condition: " + condition);
    }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public int getWarranty() { return warranty; }
    public void setWarranty(int warranty) { this.warranty = warranty; }
}
