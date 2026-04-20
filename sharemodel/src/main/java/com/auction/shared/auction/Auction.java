package com.auction.shared.auction;

import com.auction.shared.entity.Entity;
import com.auction.shared.enums.AuctionStatus;
import com.auction.shared.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Auction extends Entity {
    private String itemName;
    private String description;
    private User seller;

    private double startingPrice;
    private double currentPrice;
    private double minimumIncrement;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private AuctionStatus status;
    private User winner;


    private ArrayList<BidTransaction> bidHistory;

    public Auction(String id, String itemName, String description, User seller,
                   double startingPrice, double minimumIncrement, LocalDateTime endTime) {
        super(id);
        this.itemName = itemName;
        this.description = description;
        this.seller = seller;
        this.startingPrice = startingPrice;
        this.currentPrice = startingPrice;     // giá khởi điểm
        this.minimumIncrement = minimumIncrement;
        this.endTime = endTime;
        this.status = AuctionStatus.OPEN;      //  luôn bắt đầu từ OPEN
        this.winner = null;                    // chưa có ai thắng
        this.bidHistory = new ArrayList<>();  // lịch sử đấu giá
    }



}
