package com.auction.server.handler;

import java.util.ArrayList;

import com.auction.server.database.AuctionListDatabase;
import com.auction.server.database.createAuctionDatabase;
import com.auction.server.network.ClientHandler;
import com.auction.shared.request.Request;
import com.auction.shared.request.UpdateMainPageRequest;
import com.auction.shared.request.createAuctionRequest;
import com.auction.shared.response.Response;
import com.auction.shared.response.UpdateMainPageResponse;

public class UpdateMainPageHandler implements RequestHandler {
    public Response handle(Request request,ClientHandler clientHandler){
        UpdateMainPageRequest req = (UpdateMainPageRequest) request;
        ArrayList<Request> requestList2 = createAuctionDatabase.loadRequestList();
        for (int i = requestList2.size() - 1; i >= 0; i--){
            Request re = requestList2.get(i);
            if (re instanceof createAuctionRequest){
                createAuctionRequest req2 = (createAuctionRequest) re;
                if (req2.equals(req.getRequest())){
                    requestList2.remove(i);

                }
                else{
                    System.out.println("Need another method to compare");
                }
            }
        }
        createAuctionDatabase.saveAuctionRequest(requestList2);
        ArrayList<Request> requestList3 = createAuctionDatabase.loadRequestList();
        System.out.println(requestList3.size()); 
        
        for (ClientHandler user:ClientHandler.getOnlineUser() ){
            try{
                user.getOutputStream().writeObject(req.getRequest());
            }
            catch (Exception e){
                //Nho client dot nhien offline
                //Luu vao database
                continue;

                
            }
        }
        ArrayList<Request> requestList = AuctionListDatabase.loadAuctionList();
        requestList.add(req.getRequest());
        AuctionListDatabase.saveAuction(requestList);

        return new UpdateMainPageResponse(true);
        
    }

    
}
