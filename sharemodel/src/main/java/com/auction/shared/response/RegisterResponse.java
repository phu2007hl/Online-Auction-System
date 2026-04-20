package com.auction.shared.response;

import com.auction.shared.model.User;

import java.io.Serializable;


public class RegisterResponse extends Response implements Serializable  {

    private boolean valid;
    private User currentUser;
    public RegisterResponse(boolean valid, User currentUser) {
        this.valid = valid;
        this.currentUser = currentUser;
    }

    public boolean getResponse() {
        return valid;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}