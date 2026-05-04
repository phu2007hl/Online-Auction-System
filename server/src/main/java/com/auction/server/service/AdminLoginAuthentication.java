package com.auction.server.service;

import com.auction.shared.request.AdminLoginRequest;
import com.auction.shared.request.Request;
import com.auction.shared.response.AdminLoginResponse;
import com.auction.shared.response.Response;

public class AdminLoginAuthentication{
    private AdminLoginRequest request;
    public AdminLoginAuthentication(Request request){
        this.request = (AdminLoginRequest) request;
    }
    public boolean matchPassword(){
        if (request.getPassword().equals("9999999999")){
            return true;
        }
        else{
            return false;
        }
    }
    public Response createResponse(){
        if (matchPassword()){
            return new AdminLoginResponse(true);
        }
        else{
            return new AdminLoginResponse(false);
        }
    }
}