package com.auction.shared.request;

import com.auction.shared.model.User;

public class UpdateUserRequest extends Request {
    private User user;
    private boolean valid;

    public UpdateUserRequest(User user, boolean valid){
        this.user = user;
        this.valid = valid;
    }
    public User getUser(){
        return user;
    }
    public boolean getAdminResponse(){
        return valid;
    }
    
}
