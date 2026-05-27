package com.auction.shared.request.auction;

import com.auction.shared.model.User;
import com.auction.shared.request.Request;

public class SendMessageRequest extends Request {
    private String message;
    private int auctionId;
    private User sender;
    public SendMessageRequest(String message,int auctionId, User sender){
        this.message = message;
        this.auctionId = auctionId;
        this.sender = sender;
    }
    public String getMessage(){
        return message;
    }
    public int getAuctionId(){
        return auctionId;
    }
    public User getSender(){
        return sender;
    }
    
}
