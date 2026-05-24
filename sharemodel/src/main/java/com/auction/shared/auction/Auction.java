package com.auction.shared.auction;

import com.auction.shared.entity.Entity;
import com.auction.shared.enums.AuctionStatus;
import com.auction.shared.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

/**
* Thông tin một phiên đấu giá.
*/
public class Auction extends Entity {
  private String itemName;
  private String description;
  private User seller;
  private double startingPrice;
  private double currentPrice;
  private double minimumIncrement;
  private LocalDate startTime;
  private LocalDate endTime;
  private AuctionStatus status;
  private User winner;
  private ArrayList<BidTransaction> bidHistory;
  private byte[] imageContent;
  private String category;

  /**
  * Tạo phiên đấu giá mới.
  *
  * @param id mã phiên đấu giá
  * @param itemName tên sản phẩm
  * @param description mô tả sản phẩm
  * @param seller người bán
  * @param startingPrice giá khởi điểm
  * @param minimumIncrement bước giá tối thiểu
  * @param endTime thời điểm kết thúc
  */
  public Auction(
      int id,
      String itemName,
      String description,
      User seller,
      double startingPrice,
      double minimumIncrement,
      LocalDate endTime,
      byte[] imageContent,
      String category) {
    super(id);
    this.itemName = itemName;
    this.description = description;
    this.seller = seller;
    this.startingPrice = startingPrice;
    this.currentPrice = startingPrice;
    this.minimumIncrement = minimumIncrement;
    this.endTime = endTime;
    this.status = AuctionStatus.OPEN;
    this.winner = null;
    this.bidHistory = new ArrayList<>();
    this.imageContent = imageContent;
    this.category = category;
  }
  public int getId(){
    return id;
  }
  public String getItemName(){
    return itemName;
  }
  public String getDescription(){
    return description;
  }
  public User getSeller(){
    return seller;
  }
  public double getStartingPrice(){
    return startingPrice;
  }
  public double getMinimumIncrement(){
    return minimumIncrement;
  }
  public LocalDate getEndTime(){
    return endTime;
  }
  public LocalDate getStartTime(){
    return startTime;
  }
  public User getWinner(){
    return winner;
  }
  public AuctionStatus getStatus(){
    return status;
  }
  public ArrayList<BidTransaction> getBidHistory(){
    return bidHistory;

  }
  public byte[] getImageContent(){
    return imageContent;
  }
  public String getCategory(){
    return category;
  }
  public void setId(int id){
    this.id = id;

  }
  public double getCurrentPrice(){
    return currentPrice;
  }
  public void setItemName(String itemName){
    this.itemName = itemName;
  }
  public void setWinner(User winner){
    this.winner = winner;
  }
  public void setStatus(AuctionStatus status){
    this.status = status;


  }
  public void setCurrentPrice(double currentPrice){
    this.currentPrice = currentPrice;
  }
  public void setCategory(String category){
    this.category = category;
  }
  public void setDescription(String description){
    this.description = description;
  }
  public void setImageContent(byte[] imageContent){
    this.imageContent = imageContent;
  }
  

  
}
