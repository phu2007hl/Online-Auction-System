package com.auction.server.model.auction;

import com.auction.shared.auction.Auction;
import com.auction.shared.enums.BidResponseStatus;

public class BidProcessResult {
    private BidResponseStatus bidResponseStatus;

    public BidProcessResult(BidResponseStatus bidResponseStatus) {
        this.bidResponseStatus = bidResponseStatus;
    }

    public BidResponseStatus getBidResponseStatus() {
        return bidResponseStatus;
    }
}
