package com.auction.shared.request;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.Arrays;
import com.auction.shared.model.User;

public class createAuctionRequest extends Request{
    private byte[] imageContent;
    private String category;
    private String startingPrice;
    private String description;
    private LocalDate enDate;
    private transient ObjectOutputStream out;
    private transient ObjectInputStream in;
    private User user;
    public createAuctionRequest(byte[] imageContent, String category, String startingPrice,String description,LocalDate enDate){
        this.imageContent = imageContent;
        this.category = category;
        this.startingPrice = startingPrice;
        this.description = description;
        this.enDate = enDate;
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
    public ObjectInputStream getInStream(){
        return in;
    }
    public ObjectOutputStream getOutStream(){
        return out;
    }
    public byte[] getImageContent(){
        return imageContent;
    }
    public String getCategory(){
        return category;
    }
    public String getStartingPrice(){
        return startingPrice;
    }
    public String getDescription(){
        return description;
    }
    public LocalDate getEndDate(){
        return enDate;
    }
    @Override
    public boolean equals(Object o){
        createAuctionRequest request = (createAuctionRequest) o;
        if (category.equals(request.getCategory()) && startingPrice.equals(request.getStartingPrice()) && enDate.equals(request.getEndDate()) && description.equals(request.getDescription()) && Arrays.equals(imageContent,request.getImageContent())){
            return true;
        }
        else{
            return false;
        }
    }

}