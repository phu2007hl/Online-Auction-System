package com.auction.shared.request;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import com.auction.shared.model.User;

public abstract class  Request implements Serializable {
    private User user;
    private ObjectInputStream in;
    private ObjectOutputStream out;
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
