package com.auction.shared.response.auction;

import com.auction.shared.response.Response;

public class AuctionAutoCloseResponse extends Response {
    private boolean succes;

    public AuctionAutoCloseResponse(boolean succes) {
        this.succes = succes;
    }

    @Override
    public boolean getResponse() {
        return succes;
    }
}
