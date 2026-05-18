package com.auction.shared.request.auction;

import com.auction.shared.enums.BidResponseStatus;
import com.auction.shared.model.User;
import com.auction.shared.request.Request;

public class BidRequest extends Request {
    private int auctionId;
    private double bidPrice;
    private User bidder;
    public BidRequest(int auctionId, double bidPrice, User bidder){
        this.auctionId = auctionId;
        this.bidPrice = bidPrice;
        this.bidder = bidder;

    }
    public int getAuctionId(){
        return auctionId;
    }
    public double getBidPrice(){
        return bidPrice;
    }
    public User getBidder(){
        return bidder;
    }

    
}
