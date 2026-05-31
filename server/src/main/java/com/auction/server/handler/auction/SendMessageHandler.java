package com.auction.server.handler.auction;

import com.auction.server.handler.RequestHandler;
import com.auction.server.network.ClientHandler;
import com.auction.server.service.auction.AuctionRoomManager;
import com.auction.shared.request.Request;
import com.auction.shared.request.auction.SendMessageRequest;
import com.auction.shared.response.Response;
import com.auction.shared.response.auction.SendMessageResponse;

public class SendMessageHandler implements RequestHandler {
    public Response handle(Request request,ClientHandler clientHandler){
        SendMessageRequest req = (SendMessageRequest) request;
        SendMessageResponse response = new SendMessageResponse(req);
        AuctionRoomManager.broadcast(req.getAuctionId(), response);
        return null;
        
    }
}
