package com.auction.shared.response;

public class ToDatabaseResponse extends Response{
    private boolean valid;
    public ToDatabaseResponse(boolean valid){
        this.valid = valid;
    }
    public boolean getResponse(){
        return valid;
    }
}