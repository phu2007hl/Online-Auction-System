package com.auction.shared.request.auction;

import com.auction.shared.request.Request;

public class JoinRoomRequest extends Request {
    private int auctionId;

    public JoinRoomRequest(int auctionId) {
        this.auctionId = auctionId;
    }

    public int getAuctionId() {
        return auctionId;
    }

}
