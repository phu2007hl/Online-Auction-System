package com.auction.shared.response.auction;

import java.util.ArrayList;

import com.auction.shared.auction.BidTransaction;
import com.auction.shared.enums.BidResponseStatus;
import com.auction.shared.enums.BidderStatus;
import com.auction.shared.response.Response;


public class ParticipantResponse extends Response {
    private BidderStatus status;
    private BidResponseStatus requestStatus;
    private double currentPrice;
    private ArrayList<BidTransaction> bidHistory;
    public ParticipantResponse(double currentPrice, ArrayList<BidTransaction> bidHistory, BidderStatus status){
        this.currentPrice = currentPrice;
        this.bidHistory = bidHistory;
        this.status = status;
        
    }
    public ParticipantResponse(double currentPrice, ArrayList<BidTransaction> bidHistory, BidResponseStatus requestStatus){
        this.currentPrice = currentPrice;
        this.bidHistory = bidHistory;
        this.requestStatus = requestStatus;

    }
    public ParticipantResponse(double currentPrice, ArrayList<BidTransaction> bidHistory){
        this.currentPrice = currentPrice;
        this.bidHistory = bidHistory;

    }
    public boolean getResponse(){
        return true;
    }
    public BidderStatus getParticipantStatus(){
        return status;
    }
    public double getCurrentPrice(){
        return currentPrice;
    }
    public ArrayList<BidTransaction> getBidHistory(){
        return bidHistory;
    }
    public BidResponseStatus getRequestStatus(){
        return requestStatus;
    }
    
}
