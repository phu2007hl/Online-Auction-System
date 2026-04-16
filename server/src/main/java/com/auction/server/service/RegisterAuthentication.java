package com.auction.server.service;


import com.auction.server.database.UserDatabase;
import com.auction.shared.model.User;
import com.auction.shared.request.Request;
import com.auction.shared.request.RegisterRequest;
import com.auction.shared.response.RegisterResponse;

import java.util.HashMap;

public class RegisterAuthentication {
    private Request request;
    public RegisterAuthentication(Request request) {
        this.request =  request;
    }

    public boolean authenticateRegistration() {
        HashMap<String, User> userdata = UserDatabase.loadUser();
        RegisterRequest regReq = (RegisterRequest) request;

        if (userdata.containsKey(regReq.getEmail())) {
            return false;
        }

        User user = new User(regReq.getEmail(), regReq.getPassword(), regReq.getUsername());
        userdata.put(regReq.getEmail(), user);
        UserDatabase.saveUser(userdata);

        return true;
    }
    public RegisterResponse createResponse() {
        if (authenticateRegistration()) {
            return new RegisterResponse(true);
        } else {
            return new RegisterResponse(false);
        }
    }
}
