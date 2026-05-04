package com.auction.shared.response;

import java.util.ArrayList;
import java.util.HashMap;

import com.auction.shared.model.User;
import com.auction.shared.request.Request;

public class LoadAuctionResponse extends Response {
    private boolean valid;
    private ArrayList<Request> auctionList;
    public LoadAuctionResponse(boolean valid,ArrayList<Request> auctionList){
        this.valid = valid;
        this.auctionList = auctionList;
    }
    public boolean getResponse(){
        return valid;
    }
    public ArrayList<Request> getAuctionList(){
        return auctionList;
    }
    
}
