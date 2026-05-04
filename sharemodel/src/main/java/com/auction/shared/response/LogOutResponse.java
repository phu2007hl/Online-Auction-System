package com.auction.shared.response;

public class LogOutResponse extends Response {
    private boolean valid;
    public LogOutResponse(boolean valid){
        this.valid = valid;
    }
    public boolean getResponse(){
        return valid;
    }
    
}
