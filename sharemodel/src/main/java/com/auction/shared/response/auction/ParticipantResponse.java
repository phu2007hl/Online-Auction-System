package com.auction.shared.response.auction;

import java.util.ArrayList;

import com.auction.shared.auction.BidTransaction;
import com.auction.shared.enums.BidRequestStatus;
import com.auction.shared.enums.ParticipantStatus;
import com.auction.shared.response.Response;


public class ParticipantResponse extends Response {
    private ParticipantStatus status;
    private BidRequestStatus requestStatus;
    private double currentPrice;
    private ArrayList<BidTransaction> bidHistory;
    public ParticipantResponse(double currentPrice, ArrayList<BidTransaction> bidHistory, ParticipantStatus status){
        this.currentPrice = currentPrice;
        this.bidHistory = bidHistory;
        this.status = status;
        
    }
    public ParticipantResponse(double currentPrice,ArrayList<BidTransaction> bidHistory,BidRequestStatus requestStatus){
        this.currentPrice = currentPrice;
        this.bidHistory = bidHistory;
        this.status = status;

    }
    public boolean getResponse(){
        return true;
    }
    public ParticipantStatus getParticipantStatus(){
        return status;
    }
    public double getCurrentPrice(){
        return currentPrice;
    }
    public ArrayList<BidTransaction> getBidHistory(){
        return bidHistory;
    }
    public BidRequestStatus getRequestStatus(){
        return requestStatus;
    }
    
}
