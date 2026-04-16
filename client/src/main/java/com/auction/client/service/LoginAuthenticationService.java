package com.auction.client.service;

import com.auction.shared.request.LoginRequest;
import com.auction.shared.request.Request;

public class LoginAuthenticationService {
    private String email;
    private String password;
    public LoginAuthenticationService(String email,String password){
        this.email = email;
        this.password = password;
    }
    public String getEmail(){
        return email;
    }
    public String getPassword(){
        return password;
    }
    public boolean getEmailAuthentication(){
        String criteria = "@gmail.com";
        boolean exists = email.contains(criteria);
        return exists;
    }
    public boolean getPasswordAuthentication(){
        return password.length() > 8;
    }
    public Request createAuthRequest(){
        if (getEmailAuthentication() == true && getPasswordAuthentication() == true){
            return new LoginRequest(email, password);

        }
        else{
            return null;
        }
    }
    
}