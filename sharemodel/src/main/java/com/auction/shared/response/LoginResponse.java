package com.auction.shared.response;

import java.io.Serializable;
import com.auction.shared.enums.LoginResponseStatus;
import com.auction.shared.model.User;

public class LoginResponse extends Response implements Serializable  {

    private boolean valid;
    private LoginResponseStatus status;
    private User currentUser;
    public LoginResponse(boolean valid, LoginResponseStatus status, User currentUser) {
        this.valid = valid;
        this.status = status;
        this.currentUser = currentUser;
    }

    public boolean getResponse() {
        return valid;
    }

    public LoginResponseStatus getStatus() {
        return status;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}