package com.auction.shared.request.auction;

import com.auction.shared.request.Request;

public class GetAuctionDetailRequest extends Request {
    private int auctionId;

    public GetAuctionDetailRequest(int auctionId) {
        this.auctionId = auctionId;
    }

    public int getAuctionId() {
        return auctionId;
    }
}
