package com.auction.shared.request;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.auction.shared.model.User;

public class LoginRequest extends Request {
    private String email;
    private String password;
    private User user;
    private transient ObjectOutputStream out;
    private transient ObjectInputStream in;
    
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
    public void setUser(User user){
        this.user = user;
    }
    public void setOutputStream(ObjectOutputStream out){
        this.out = out;
    }
    public void setInputStream(ObjectInputStream in){
        this.in = in;
    }
    public User getUser(){
        return user;
    }
    public ObjectOutputStream getOutStream(){
        return out;
    }
    public ObjectInputStream getInStream(){
        return in;
    }
}
