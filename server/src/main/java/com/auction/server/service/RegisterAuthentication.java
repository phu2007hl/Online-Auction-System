package com.auction.server.service;


import com.auction.server.database.UserDatabase;
import com.auction.shared.model.User;
import com.auction.shared.request.Request;
import com.auction.shared.request.RegisterRequest;
import com.auction.shared.response.RegisterResponse;

import java.util.HashMap;

public class RegisterAuthentication {
    private RegisterRequest request;
    private static HashMap<String, User> userdata = UserDatabase.loadUser();
    public RegisterAuthentication(Request request) {
        this.request = (RegisterRequest) request;
    }

    public boolean authenticateRegistration() {
        

        if (userdata.containsKey(request.getEmail())) {
            return false;
        }

        User user = new User(request.getEmail(), request.getPassword(), request.getUsername());
        userdata.put(request.getEmail(), user);
        UserDatabase.saveUser(userdata);

        return true;
    }
    public RegisterResponse createResponse() {
        if (authenticateRegistration()) {
            return new RegisterResponse(true, getUserData());
        } else {
            return new RegisterResponse(false,null );
        }
    }
    public User getUserData(){
        return userdata.get(request.getEmail());
    }
}
