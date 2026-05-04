package com.auction.server.database;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.auction.shared.model.User;

public class UserDatabase {
    private static ConcurrentHashMap<String,User> userData;
    public static void saveUser(ConcurrentHashMap<String, User> userData){
        try{
            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("User.ser")));
            out.writeObject(userData);
            out.close();
            

        }
        catch (Exception e){
            e.printStackTrace();
            
        }
    }
    public static ConcurrentHashMap<String,User> loadUser(){
        try{
            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream("User.ser")));
            ConcurrentHashMap<String,User>  userData = (ConcurrentHashMap<String,User>) in.readObject();
            in.close();
            return userData;
            

        }
        catch (Exception e){
            e.printStackTrace();
            return new ConcurrentHashMap<String,User>();
        }
    }
    public static ConcurrentHashMap<String,User> getUserData(){
        return userData;

    }
    public static void setUserData(ConcurrentHashMap<String,User> data){
        userData = data;
    }
    
}
