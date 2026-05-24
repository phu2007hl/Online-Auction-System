package com.auction.shared.response.auction;

import com.auction.shared.auction.Auction;
import com.auction.shared.request.admin.EditAuctionRequest;
import com.auction.shared.response.Response;

public class UpdateAuctionResponse extends Response {
    private boolean success;
    private EditAuctionRequest auction;
    public UpdateAuctionResponse(EditAuctionRequest auction){
        this.auction = auction;
    }
    public boolean getResponse(){
        return true;

    }
    public EditAuctionRequest getUpdatedAuction(){
        return auction;
    }
}
