package com.auction.shared.request;

public class LoginRequest extends Request {
    private String email;
    private String password;
    public LoginRequest(String email, String password){
        this.email = email;
        this.password = password;
    }
    public String getPassword(){
        return password;
    }
    public String getEmail(){
        return email;
    }
    
}
