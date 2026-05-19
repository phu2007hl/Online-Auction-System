package com.auction.server.handler.auction;

import com.auction.server.handler.RequestHandler;
import com.auction.server.network.ClientHandler;
import com.auction.server.service.auction.AuctionRoomManager;
import com.auction.shared.request.Request;
import com.auction.shared.request.auction.LeaveRoomRequest;
import com.auction.shared.response.Response;
import com.auction.shared.response.auction.LeaveRoomResponse;

public class LeaveRoomRequestHandler implements RequestHandler{
    @Override
    public Response handle(Request request, ClientHandler clientHandler) {
        LeaveRoomRequest leaveRoomRequest = (LeaveRoomRequest) request;
        boolean leaveRoomSucceeded = AuctionRoomManager.leaveRoom(leaveRoomRequest.getAuctionId(), clientHandler);
        if(leaveRoomSucceeded){
            return new LeaveRoomResponse(true, "Leave room thành công");
        }
        return new LeaveRoomResponse(false, "Leave room thất bại");
    }
}
