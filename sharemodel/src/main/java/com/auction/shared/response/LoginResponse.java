package com.auction.shared.response;

import java.io.Serializable;
import com.auction.shared.enums.LoginResponseStatus;
public class LoginResponse extends Response implements Serializable  {

    private boolean valid;
    private LoginResponseStatus status;

    public LoginResponse(boolean valid, LoginResponseStatus status) {
        this.valid = valid;
        this.status = status;
    }

    public boolean getResponse() {
        return valid;
    }

    public LoginResponseStatus getStatus() {
        return status;
    }
}