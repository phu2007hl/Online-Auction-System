package com.auction.shared.response;

import java.io.Serializable;

import com.auction.shared.model.User;


public class RegisterResponse extends Response implements Serializable  {

    private boolean valid;

    public RegisterResponse(boolean valid) {
        this.valid = valid;
    
    }

    public boolean getResponse() {
        return valid;
    }

}