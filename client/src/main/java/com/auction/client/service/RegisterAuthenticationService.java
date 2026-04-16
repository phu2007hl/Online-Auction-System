package com.auction.client.service;

import com.auction.shared.request.RegisterRequest;
import com.auction.shared.request.Request;

public class RegisterAuthenticationService {
    private String email;
    private String password;
    private String username;
    public RegisterAuthenticationService(String email,String password,String username){
        this.email = email;
        this.password = password;
        this.username = username;
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
            return new RegisterRequest(email, password, username);
        }
        else{
            return null;
        }

    }
    
}
