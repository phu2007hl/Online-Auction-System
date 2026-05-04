package com.auction.shared.request;

public class AdminLoginRequest extends Request{
    private String password;
    public AdminLoginRequest(String password){
        this.password = password;
    }
    public String  getPassword(){
        return password;
    }
}