package com.auction.shared.request.auction;

import com.auction.shared.enums.BidStatus;
import com.auction.shared.request.Request;

public class BidRequest extends Request {
    private String auctionId;
    private double bidPrice;
    private BidStatus status;
    public BidRequest(String auctionId, double bidPrice, BidStatus status){
        this.auctionId = auctionId;
        this.bidPrice = bidPrice;
        this.status = BidStatus.PENDING;

    }
    public void setBidPrice(double price){
        this.bidPrice = price;
    }
    public void setAuctionId(String id){
        this.auctionId = id;
    }
    public String getAuctionId(){
        return auctionId;
    }
    public double getBidPrice(){
        return bidPrice;
    }
    
}
