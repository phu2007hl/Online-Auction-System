package com.auction.shared.request;

public class ToDatabaseRequest extends Request {
    private Request request;
    public ToDatabaseRequest(Request request){
        this.request = request;
    }
    public Request getRequest(){
        return request;
    }
}
