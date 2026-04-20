package com.auction.server.database;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import com.auction.shared.model.User;

public class UserDatabase {
    public static void saveUser(HashMap<String, User> userData){
        try{
            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("User.ser")));
            out.writeObject(userData);
            out.close();
            

        }
        catch (Exception e){
            e.printStackTrace();
            
        }
    }
    public static HashMap<String,User> loadUser(){
        try{
            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream("User.ser")));
            HashMap<String,User>  userData = (HashMap<String,User>) in.readObject();
            in.close();
            return userData;
            

        }
        catch (Exception e){
            e.printStackTrace();
            return new HashMap<String,User>();
        }
    }
    
}
