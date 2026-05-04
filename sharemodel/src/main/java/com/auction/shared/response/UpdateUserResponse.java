package com.auction.shared.response;

public class UpdateUserResponse extends Response{
    private boolean valid;
    public UpdateUserResponse(boolean valid){
        this.valid = valid;
    }
    public boolean getResponse(){
        return valid;
    }
}