package com.auction.shared.request;

public class RegisterRequest extends  Request {
    private String email;
    private String password;
    private String username;
    public RegisterRequest(String email, String password, String username ){
        this.email = email;
        this.password = password;
        this.username = username;
    }
    public String getEmail(){
        return email;
       
        
    }
    public String getUsername(){
        return username;
    }
    public String getPassword(){
        return password;
    }
}
