package com.auction.shared.response.admin;

import com.auction.shared.request.auction.PendingAuctionReviewRequest;
import com.auction.shared.response.Response;

import java.util.LinkedHashMap;

public class GetCheckedAuctionListResponse extends Response {
    private final LinkedHashMap<Integer, PendingAuctionReviewRequest> checkedRequestList;
    private final boolean valid;
    @Override
    public boolean getResponse() {
        return valid;
    }
    public GetCheckedAuctionListResponse(boolean valid, LinkedHashMap<Integer, PendingAuctionReviewRequest> checkedRequestList){
        this.checkedRequestList=checkedRequestList;
        this.valid = valid;
    }

    public LinkedHashMap<Integer, PendingAuctionReviewRequest> getCheckedRequestList() {
        return checkedRequestList;
    }
}
