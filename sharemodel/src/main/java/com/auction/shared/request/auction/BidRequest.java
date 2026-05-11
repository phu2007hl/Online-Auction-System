package com.auction.shared.request.auction;

import com.auction.shared.enums.BidStatus;
import com.auction.shared.request.Request;

public class BidRequest extends Request {
    private int auctionId;
    private double bidPrice;
    private BidStatus status;
    public BidRequest(int auctionId, double bidPrice, BidStatus status){
        this.auctionId = auctionId;
        this.bidPrice = bidPrice;
        this.status = BidStatus.PENDING;

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
    
}
