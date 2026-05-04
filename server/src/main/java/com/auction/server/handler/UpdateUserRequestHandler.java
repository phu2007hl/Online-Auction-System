package com.auction.server.handler;

import java.util.HashMap;

import com.auction.server.network.ClientHandler;
import com.auction.shared.model.User;
import com.auction.shared.request.Request;
import com.auction.shared.request.UpdateUserRequest;
import com.auction.shared.response.Response;
import com.auction.shared.response.UpdateUserResponse;

public class UpdateUserRequestHandler implements RequestHandler {
    public Response handle(Request request, ClientHandler clienthandler){
        HashMap<User,ClientHandler> map = ClientHandler.getMap();
        UpdateUserRequest req = (UpdateUserRequest) request;
        try{
            map.get(req.getUser()).getOutputStream().writeObject(req);
        }
        catch (Exception e){
            e.printStackTrace();
            //save to database
            

        }
        return new UpdateUserResponse(true);
        
    }
    
}
