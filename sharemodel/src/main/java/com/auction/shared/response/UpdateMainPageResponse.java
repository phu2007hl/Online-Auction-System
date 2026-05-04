package com.auction.shared.response;

public class UpdateMainPageResponse extends Response {
    private boolean valid;
    public UpdateMainPageResponse(boolean valid){
        this.valid = valid;
    }
    public boolean getResponse(){
        return valid;
    }
    
}
