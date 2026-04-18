package com.auction.shared.auction;

import com.auction.shared.entity.Entity;
import com.auction.shared.model.User;

public class BidTransaction extends Entity {

    private User bidder;
    private double bidAmount;
    private String auctionId;

    public BidTransaction(String id, User bidder, double bidAmount, String auctionId) {
        super(id);
        this.bidder = bidder;
        this.bidAmount = bidAmount;
        this.auctionId = auctionId;
    }

    public User getBidder() {
        return bidder;
    }

    public double getBidAmount() {
        return bidAmount;
    }

    public String getAuctionId() {
        return auctionId;
    }


}
