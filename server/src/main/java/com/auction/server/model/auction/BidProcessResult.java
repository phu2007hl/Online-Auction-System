package com.auction.server.model.auction;

import com.auction.shared.auction.Auction;
import com.auction.shared.enums.BidResponseStatus;
import com.auction.shared.response.auction.BidUpdateResponse;

public class BidProcessResult {
    private BidResponseStatus bidResponseStatus;
    private BidUpdateResponse bidUpdateResponse;

    public BidProcessResult(BidResponseStatus bidResponseStatus, BidUpdateResponse bidUpdateResponse) {
        this.bidResponseStatus = bidResponseStatus;
        this.bidUpdateResponse = bidUpdateResponse;
    }

    public BidResponseStatus getBidResponseStatus() {
        return bidResponseStatus;
    }

    public BidUpdateResponse getBidUpdateResponse() {
        return bidUpdateResponse;
    }
}
