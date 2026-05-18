package com.auction.shared.response.auction;

import com.auction.shared.enums.BidResponseStatus;
import com.auction.shared.response.Response;

public class BidResultResponse extends Response {
    private BidResponseStatus bidResponseStatus;
    private String message;

    public BidResultResponse(BidResponseStatus bidResponseStatus, String message) {
        this.bidResponseStatus = bidResponseStatus;
        this.message = message;
    }

    public BidResponseStatus getBidResponseStatus() {
        return bidResponseStatus;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean getResponse() {
        return true;
    }
}
