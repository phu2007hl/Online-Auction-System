package com.auction.server.service;
import java.util.HashMap;

import com.auction.shared.request.Request;
import com.auction.shared.request.LoginRequest;
import com.auction.server.database.UserDatabase;
import com.auction.shared.response.Response;
import com.auction.shared.response.LoginResponse;
import com.auction.shared.model.User;

public class LoginAuthentication {
    private Request request;

    public LoginAuthentication(Request request) {
        this.request = request;
    }

    public boolean authenticateLogin() {
        HashMap<String, User> userdata = UserDatabase.loadUser();

        if (!userdata.containsKey(((LoginRequest) request).getEmail())) {
            return false;
        }

        User user = userdata.get(((LoginRequest) request).getEmail());
        return user.getPassword().equals(((LoginRequest) request).getPassword());
    }

    public Response createResponse() {
        if (authenticateLogin()){
            LoginResponse response = new LoginResponse(true);
            return response;
        }
        else{
            LoginResponse response = new LoginResponse(false);
            return response;
        }
    }
}
