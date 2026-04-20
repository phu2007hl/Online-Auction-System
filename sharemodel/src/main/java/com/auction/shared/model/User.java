package com.auction.shared.model;

import java.io.Serializable;

public class User implements Serializable { //add login/register info before adding other info like bidHistory
    private String email;
    private String password;
    private String username;
    public User(String email,String password,String username){
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
    public String getUsername(){
        return username;
    }
    
}