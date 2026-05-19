package com.auction.shared.request.auction;

import com.auction.shared.request.Request;

public class LeaveRoomRequest extends Request {
    private int auctionId;

    public LeaveRoomRequest(int auctionId) {
        this.auctionId = auctionId;
    }

    public int getAuctionId() {
        return auctionId;
    }
}
