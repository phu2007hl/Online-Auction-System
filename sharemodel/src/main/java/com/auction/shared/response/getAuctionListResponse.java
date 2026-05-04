package com.auction.shared.response;

import java.util.ArrayList;
import java.util.HashMap;

import com.auction.shared.request.Request;

public class getAuctionListResponse extends Response {
    private ArrayList<Request> requestList;
    private boolean valid;
    public getAuctionListResponse(boolean valid, ArrayList<Request> requestList){
        this.requestList = requestList;
        this.valid = valid;
    }
    public boolean getResponse(){
        return valid;
    }
    public ArrayList<Request> getList(){
        return requestList;
    }

    
}
