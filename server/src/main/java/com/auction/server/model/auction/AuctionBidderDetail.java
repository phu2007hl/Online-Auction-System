package com.auction.server.model.auction;

import com.auction.shared.enums.BidderStatus;

import java.io.Serializable;
import java.util.HashMap;

public class AuctionBidderDetail implements Serializable {

    private String currentWinnerEmail;
    private HashMap<String, BidderStatus> bidderStatusHashMap;

    public AuctionBidderDetail(String currentWinnerEmail, HashMap<String, BidderStatus> bidderStatusHashMap) {
        this.currentWinnerEmail = currentWinnerEmail;
        this.bidderStatusHashMap = bidderStatusHashMap;
    }

    public String getCurrentWinnerEmail() {
        return currentWinnerEmail;
    }

    public HashMap<String, BidderStatus> getBidderStatusHashMap() {
        return bidderStatusHashMap;
    }

    public void setCurrentWinnerEmail(String currentWinnerEmail) {
        this.currentWinnerEmail = currentWinnerEmail;
    }
}
