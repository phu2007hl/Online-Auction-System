package com.auction.server.handler;

import java.util.ArrayList;
import java.util.HashMap;

import com.auction.server.database.AdminResponseDatabase;
import com.auction.server.database.AuctionListDatabase;
import com.auction.server.database.createAuctionDatabase;
import com.auction.server.network.ClientHandler;
import com.auction.shared.model.User;
import com.auction.shared.request.Request;
import com.auction.shared.response.LoadAuctionResponse;
import com.auction.shared.response.Response;

public class LoadAuctionHandler implements RequestHandler {
    public Response handle(Request request,ClientHandler clientHandler){
        ArrayList<Request> auctionList = AuctionListDatabase.loadAuctionList();
        return new LoadAuctionResponse(true, auctionList);
    }

    
}
