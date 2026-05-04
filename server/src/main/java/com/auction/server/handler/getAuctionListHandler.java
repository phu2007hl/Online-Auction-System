package com.auction.server.handler;

import java.util.ArrayList;
import java.util.HashMap;

import com.auction.server.database.createAuctionDatabase;
import com.auction.server.network.ClientHandler;
import com.auction.shared.request.Request;
import com.auction.shared.response.Response;
import com.auction.shared.response.getAuctionListResponse;

public class getAuctionListHandler implements RequestHandler {
    public Response handle(Request request, ClientHandler clienthandler){
        ArrayList<Request> requestList = createAuctionDatabase.loadRequestList();
        getAuctionListResponse response = new getAuctionListResponse(true, requestList);
        return response;
    }
    
}
