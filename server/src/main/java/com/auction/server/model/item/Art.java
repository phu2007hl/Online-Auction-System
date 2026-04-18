package com.auction.server.model.item;

import com.auction.shared.enums.ItemCondition;
import com.auction.shared.model.User;

public class Art extends Item {
    private String artist;
    private int year;

    public Art(String id, String name, String description, ItemCondition condition,
               String imageUrl, User seller, String artist, int year) {
        super(id, name, description, condition, imageUrl, seller);
        this.artist = artist;
        this.year = year;
    }

    @Override
    public void printInfo() {
        System.out.println("[Art] " + name + " | Artist: " + artist +
                " | Year: " + year + " | Condition: " + condition);
    }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
}
