package com.auction.shared.request.auction;

import com.auction.shared.enums.AuctionStatus;
import com.auction.shared.enums.BidRequestStatus;
import com.auction.shared.enums.BidRequestStatus;
import com.auction.shared.model.User;
import com.auction.shared.request.Request;

public class BidRequest extends Request {
    private int auctionId;
    private double bidPrice;
    private BidRequestStatus status;
    private User bidder;
    public BidRequest(int auctionId, double bidPrice, BidRequestStatus status, User bidder){
        this.auctionId = auctionId;
        this.bidPrice = bidPrice;
        this.status = BidRequestStatus.PENDING;
        this.bidder = bidder;

    }
    public void setBidPrice(double price){
        this.bidPrice = price;
    }
    public void setAuctionId(int id){
        this.auctionId = id;
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
    public void setStatus(BidRequestStatus status){
        this.status = status;

    }
    public BidRequestStatus getBidRequestStatus(){
        return status;
    }

    
}
