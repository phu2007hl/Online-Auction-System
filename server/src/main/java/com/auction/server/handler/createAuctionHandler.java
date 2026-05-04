package com.auction.server.handler;

import java.util.ArrayList;
import java.util.HashMap;

import com.auction.server.database.createAuctionDatabase;
import com.auction.server.network.ClientHandler;
import com.auction.shared.request.Request;
import com.auction.shared.request.createAuctionRequest;
import com.auction.shared.response.Response;
import com.auction.shared.response.createAuctionResponse;

public class createAuctionHandler implements RequestHandler{
    public Response handle(Request request,ClientHandler clienthandler){
        try{
            createAuctionRequest req = (createAuctionRequest) request; 
            req.setUser(clienthandler.getUser());
            ClientHandler.getAdminHandler().forwardRequest(req, clienthandler);
            ClientHandler.addRequest(clienthandler.getUser(),clienthandler);
            createAuctionResponse response = new createAuctionResponse(true);
            return response;
        }
        catch (Exception e){
            //Lưu vào database
            e.printStackTrace();
            ArrayList<Request> requestList = createAuctionDatabase.loadRequestList();
            requestList.add(request);
            createAuctionDatabase.saveAuctionRequest(requestList);
            createAuctionResponse response = new createAuctionResponse(true);
            return response;

        }

    }

}