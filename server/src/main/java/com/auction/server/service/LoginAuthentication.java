package com.auction.server.service;
import java.util.HashMap;

import com.auction.shared.enums.LoginResponseStatus;
import com.auction.shared.request.Request;
import com.auction.shared.request.LoginRequest;
import com.auction.server.database.UserDatabase;
import com.auction.shared.response.Response;
import com.auction.shared.response.LoginResponse;
import com.auction.shared.model.User;

public class LoginAuthentication {
    private LoginRequest request;
    private HashMap<String, User> userdata = UserDatabase.loadUser();

    public LoginAuthentication(Request request) {
        this.request = (LoginRequest) request;
    }

    public boolean containsEmail() {
        return userdata.containsKey(request.getEmail());
    }

    public boolean matchPassword() {
        String userPassword = userdata.get(request.getEmail()).getPassword();
        String requestPassword = request.getPassword();
        return requestPassword.equals(userPassword);
    }

    public Response createResponse() {
        if (containsEmail()) { // email ton tai

            if (!matchPassword()) { // mat khau request tren ui khop voi mat khau co trong database
                return new LoginResponse(false, LoginResponseStatus.INVALID_PASSWORD,null);
            }
            return new LoginResponse(true, LoginResponseStatus.SUCCESS,getUserData());
        }
        else{
            return new LoginResponse(false, LoginResponseStatus.EMAIL_NOT_FOUND,null);
        }
    }
    public User getUserData(){
        return userdata.get(request.getEmail());
    }
}
