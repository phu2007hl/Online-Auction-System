package com.auction.server.database;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.auction.server.network.ClientHandler;
import com.auction.shared.request.Request;

public class  createAuctionDatabase{
    public static void saveAuctionRequest(ArrayList<Request> requestList){
        try{
            System.out.println("Saving to database");
            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("AucRequest.ser")));
            out.writeObject(requestList);
            System.out.println("Saved to database");
            out.flush();
            
        }
        catch (Exception e){
            System.out.println("Can not find the file");
            e.printStackTrace();
        }

    }
    public static ArrayList<Request> loadRequestList(){
        try{
            System.out.println("Loading list from database");
            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream("AucRequest.ser")));
            
            ArrayList<Request> requestList = (ArrayList<Request>) in.readObject();
            System.out.println("List loaded");
            in.close();
            return requestList;

        }
        catch (Exception e){
            e.printStackTrace();
            return new ArrayList<Request>();
        }

    }
    public static void removeRequest(Request request){
        ArrayList<Request> requestList = createAuctionDatabase.loadRequestList();
        requestList.remove(request);
        System.out.println("Request removed from list");
        createAuctionDatabase.saveAuctionRequest(requestList);

    }

}