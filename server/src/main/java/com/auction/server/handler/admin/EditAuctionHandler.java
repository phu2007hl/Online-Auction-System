package com.auction.server.handler.admin;

import com.auction.server.handler.RequestHandler;
import com.auction.server.network.ClientHandler;
import com.auction.server.service.admin.EditAuctionService;
import com.auction.shared.enums.CreateAuctionStatus;
import com.auction.shared.request.Request;
import com.auction.shared.request.admin.EditAuctionRequest;
import com.auction.shared.response.Response;
import com.auction.shared.response.admin.EditAuctionResponse;

public class EditAuctionHandler implements RequestHandler {
    @Override
    public Response handle(Request request, ClientHandler clientHandler) {
        EditAuctionRequest editAuctionRequest = (EditAuctionRequest) request;
        if(editAuctionRequest.getCreateAuctionStatus() == CreateAuctionStatus.PENDING){
            boolean success = EditAuctionService.editBeforeApprove(editAuctionRequest);
            return new EditAuctionResponse(success);
        }
        if(editAuctionRequest.getCreateAuctionStatus() == CreateAuctionStatus.SUCCESS){
            /// Phần update real time viết ở đây
        }
        return new EditAuctionResponse(false);
    }
}
