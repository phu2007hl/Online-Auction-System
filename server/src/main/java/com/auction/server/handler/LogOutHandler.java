package com.auction.server.handler;

import com.auction.server.network.ClientHandler;
import com.auction.shared.request.Request;
import com.auction.shared.response.LogOutResponse;
import com.auction.shared.response.Response;

public class LogOutHandler implements RequestHandler {
    public Response handle(Request request,ClientHandler clientHandler){
        ClientHandler.removeOfflineUser(clientHandler);
        return new LogOutResponse(true);
    }
    
}
