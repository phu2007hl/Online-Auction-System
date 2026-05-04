package com.auction.shared.response;

public class AdminLoginResponse extends Response{
    private boolean valid;
    public AdminLoginResponse(boolean valid){
        this.valid = valid;
    }
    public boolean getResponse(){
        return valid;
    }
}