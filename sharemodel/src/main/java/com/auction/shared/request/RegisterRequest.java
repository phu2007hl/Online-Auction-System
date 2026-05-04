package com.auction.shared.request;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.auction.shared.model.User;

public class RegisterRequest extends  Request {
    private String email;
    private String password;
    private String username;
    private User user;
    private transient ObjectOutputStream out;
    private transient ObjectInputStream in;
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
    public void setUser(User user){
        this.user = user;

    }
    public void setInputStream(ObjectInputStream in){
        this.in = in;
    }
    public void setOutputStream(ObjectOutputStream out){
        this.out = out;
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
