package com.auction.server.service;

import com.auction.shared.model.User;
import com.auction.shared.request.RegisterRequest;
import com.auction.shared.response.RegisterResponse;
import com.auction.server.database.UserDatabase;
import java.util.concurrent.ConcurrentHashMap;

public class RegisterAuthentication {
    private RegisterRequest request;
    private static ConcurrentHashMap<String, User> userdata;
    public RegisterAuthentication(RegisterRequest request) {
        this.request = request;
    }

    public boolean authenticateRegistration() {
        if (userdata == null) {
            userdata = UserDatabase.getUserData();
            if (userdata == null) {
                userdata = UserDatabase.loadUser();
                UserDatabase.setUserData(userdata);
            }
        }

        User newUser = new User(request.getEmail(), request.getPassword(), request.getUsername());
        User result = userdata.putIfAbsent(request.getEmail(), newUser);

        if (result == null) {
            UserDatabase.saveUser(userdata);
            return true;
        }
        return false;
    }

    public RegisterResponse createResponse() {
        if (authenticateRegistration()) {
            return new RegisterResponse(true, getUserData());
        } else {
            return new RegisterResponse(false, null);
        }
    }

    public User getUserData() {
        return userdata.get(request.getEmail());
    }
}