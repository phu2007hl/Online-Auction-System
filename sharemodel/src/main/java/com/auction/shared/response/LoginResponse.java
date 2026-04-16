package com.auction.shared.response;

import java.io.Serializable;


public class LoginResponse extends Response implements Serializable  {

    private boolean valid;

    public LoginResponse(boolean valid) {
        this.valid = valid;
    }

    public boolean getResponse() {
        return valid;
    }
}