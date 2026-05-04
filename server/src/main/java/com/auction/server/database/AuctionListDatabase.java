package com.auction.server.database;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.auction.shared.request.Request;

public class AuctionListDatabase {
    public static void saveAuction(ArrayList<Request> auctionList){
        try{
            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("AuctionList.ser")));
            
            out.writeObject(auctionList);
            System.out.println("Written succesfully");
            out.flush();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public static ArrayList<Request> loadAuctionList(){
        try{
            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream("AuctionList.ser")));
            ArrayList<Request> auctionList = (ArrayList<Request>) in.readObject();
            System.out.println("Load auctionList succesfully");
            in.close();
            return auctionList;
        }
        catch (Exception e){
            e.printStackTrace();
            return new ArrayList<Request>();
        }
    }
}
