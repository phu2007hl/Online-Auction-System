package com.auction.server.handler.auction;

import com.auction.server.handler.RequestHandler;
import com.auction.server.network.ClientHandler;
import com.auction.server.service.auction.AuctionRoomManager;
import com.auction.shared.request.Request;
import com.auction.shared.request.auction.JoinRoomRequest;
import com.auction.shared.response.Response;
import com.auction.shared.response.auction.JoinRoomResponse;

public class JoinRoomRequestHandler implements RequestHandler {
    @Override
    public Response handle(Request request, ClientHandler clientHandler) {
        JoinRoomRequest joinRoomRequest = (JoinRoomRequest) request;
        boolean joinRoomSucceeded = AuctionRoomManager.joinRoom(joinRoomRequest.getAuctionId(), clientHandler);
        if(joinRoomSucceeded){
            return new JoinRoomResponse(true, "Join room thành công");
        }
        return new JoinRoomResponse(false, "Join room thất bại");
    }
}
