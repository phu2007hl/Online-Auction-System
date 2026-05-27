package com.auction.shared.response.auction;

import com.auction.shared.request.auction.SendMessageRequest;
import com.auction.shared.response.Response;

public class SendMessageResponse extends Response {
    private SendMessageRequest request;
    public SendMessageResponse(SendMessageRequest request){
        this.request = request;
    }
    public SendMessageRequest getRequest(){
        return request;
    }
    public boolean getResponse(){
        return true;
    }
}
