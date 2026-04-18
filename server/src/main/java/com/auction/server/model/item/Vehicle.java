package com.auction.server.model.item;

import com.auction.shared.enums.ItemCondition;
import com.auction.shared.model.User;

public class Vehicle extends Item {
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
        System.out.println("[Vehicle] " + name + " | Brand: " + brand +
                " | Year: " + year + " | Condition: " + condition);
    }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
}
