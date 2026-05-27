package com.auction.shared.response.auction;

import com.auction.shared.auction.BidTransaction;
import com.auction.shared.response.Response;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class BidUpdateResponse extends Response {
    private int auctionId;
    private double currentPrice;
    private String currentWinnerEmail;
    private String currentWinnerUsername;
    private ArrayList<BidTransaction> bidHistory;
    private LocalDateTime endTime;

    public BidUpdateResponse(
            int auctionId,
            double currentPrice,
            String currentWinnerEmail,
            String currentWinnerUsername,
            ArrayList<BidTransaction> bidHistory) {
        this(auctionId, currentPrice, currentWinnerEmail, currentWinnerUsername, bidHistory, null);
    }

    public BidUpdateResponse(
            int auctionId,
            double currentPrice,
            String currentWinnerEmail,
            String currentWinnerUsername,
            ArrayList<BidTransaction> bidHistory,
            LocalDateTime endTime) {
        this.auctionId = auctionId;
        this.currentPrice = currentPrice;
        this.currentWinnerEmail = currentWinnerEmail;
        this.currentWinnerUsername = currentWinnerUsername;
        this.bidHistory = bidHistory;
        this.endTime = endTime;
    }

    public int getAuctionId() {
        return auctionId;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public String getCurrentWinnerEmail() {
        return currentWinnerEmail;
    }

    public String getCurrentWinnerUsername() {
        return currentWinnerUsername;
    }

    public ArrayList<BidTransaction> getBidHistory() {
        return bidHistory;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean getResponse() {
        return true;
    }
}
