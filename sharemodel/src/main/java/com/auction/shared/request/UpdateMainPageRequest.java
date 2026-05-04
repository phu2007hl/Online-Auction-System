package com.auction.shared.request;

public class UpdateMainPageRequest extends Request {
    private createAuctionRequest request;
    public UpdateMainPageRequest(createAuctionRequest request){
        this.request = request;
    }
    public createAuctionRequest getRequest(){
        return request;
    }
    
}
