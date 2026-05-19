package com.auction.shared.response.auction;

import com.auction.shared.auction.Auction;
import com.auction.shared.enums.AuctionStatus;
import com.auction.shared.enums.BidderStatus;
import com.auction.shared.response.Response;

public class GetAuctionDetailResponse extends Response {
    private boolean success;
    private Auction auction;
    private BidderStatus bidderStatus;
    private AuctionStatus auctionStatus;
    public GetAuctionDetailResponse(boolean success, Auction auction,  AuctionStatus auctionStatus, BidderStatus bidderStatus) {
        this.success = success;
        this.auction = auction;
        this.bidderStatus = bidderStatus;
        this.auctionStatus = auctionStatus;
    }

    @Override
    public boolean getResponse() {
        return success;
    }

    public Auction getAuction() {
        return auction;
    }

    public BidderStatus getBidderStatus() {
        return bidderStatus;
    }

    public AuctionStatus getAuctionStatus() {
        return auctionStatus;
    }
}
