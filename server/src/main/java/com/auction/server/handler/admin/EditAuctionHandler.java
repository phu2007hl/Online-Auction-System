package com.auction.server.handler.admin;

import com.auction.server.handler.RequestHandler;
import com.auction.server.network.ClientHandler;
import com.auction.server.service.admin.EditAuctionService;
import com.auction.server.service.auction.AuctionRoomManager;
import com.auction.shared.enums.CreateAuctionStatus;
import com.auction.shared.request.Request;
import com.auction.shared.request.admin.EditAuctionRequest;
import com.auction.shared.response.Response;
import com.auction.shared.response.admin.EditAuctionResponse;
import com.auction.shared.response.auction.UpdateAuctionResponse;

public class EditAuctionHandler implements RequestHandler {
    @Override
    public Response handle(Request request, ClientHandler clientHandler) {
        EditAuctionRequest editAuctionRequest = (EditAuctionRequest) request;
        if(editAuctionRequest.getCreateAuctionStatus() == CreateAuctionStatus.PENDING){
            boolean success = EditAuctionService.editBeforeApprove(editAuctionRequest);
            return new EditAuctionResponse(success);
        }
        if(editAuctionRequest.getCreateAuctionStatus() == CreateAuctionStatus.SUCCESS){
            boolean success = EditAuctionService.editAfterApprove(editAuctionRequest);
            AuctionRoomManager.broadcast(editAuctionRequest.getId(),new UpdateAuctionResponse(editAuctionRequest) );
            return new EditAuctionResponse(success);
            
            
        }
        return new EditAuctionResponse(false);
    }
}
