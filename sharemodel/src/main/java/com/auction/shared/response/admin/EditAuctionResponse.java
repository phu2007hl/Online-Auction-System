package com.auction.shared.response.admin;

import com.auction.shared.response.Response;

public class EditAuctionResponse extends Response {
    private boolean success;

    public EditAuctionResponse(boolean success) {
        this.success = success;
    }

    @Override
    public boolean getResponse() {
        return success;
    }
}
