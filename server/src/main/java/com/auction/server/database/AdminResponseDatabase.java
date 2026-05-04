package com.auction.server.database;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import com.auction.shared.model.User;
import com.auction.shared.request.Request;

public class AdminResponseDatabase {
    public static void saveAdminResponse(HashMap<User,Request> adminResponse){
        try{
            System.out.println("Saving to file");
            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("AdminResponse.ser")));
            out.writeObject(adminResponse);
            System.out.println("Saved to file");
            out.flush();
        }
        catch (Exception e){
            System.out.println("Can not find the file");
            e.printStackTrace();
        }
    }
    public static HashMap<User,Request> loadAdminResponse(){
        try{
            System.out.println("Loading from file");
            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream("AdminResponse.ser")));
            HashMap<User,Request> adminResponse = (HashMap<User,Request>) in.readObject();
            System.out.println("Loaded succesfully");
            in.close();
            return adminResponse;
            
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.print("There are some error either saving or loading");
            return new HashMap<User,Request>();
        }
    }

    
}
