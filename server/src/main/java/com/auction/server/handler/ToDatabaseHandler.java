package com.auction.server.handler;

import java.util.HashMap;

import com.auction.server.database.AdminResponseDatabase;
import com.auction.server.network.ClientHandler;
import com.auction.shared.model.User;
import com.auction.shared.request.Request;
import com.auction.shared.response.Response;
import com.auction.shared.response.ToDatabaseResponse;

public class ToDatabaseHandler implements RequestHandler{
    public Response handle(Request request,ClientHandler clientHandler){
        HashMap<User,Request> adminResponse = AdminResponseDatabase.loadAdminResponse();
        adminResponse.put(request.getUser(),request);
        AdminResponseDatabase.saveAdminResponse(adminResponse);
        return new ToDatabaseResponse(true);

    }
    
}
