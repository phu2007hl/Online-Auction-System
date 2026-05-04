package com.auction.server.handler;

import com.auction.server.network.ClientHandler;
import com.auction.shared.request.Request;
import com.auction.shared.response.Response;

public interface RequestHandler {
    public Response handle(Request request, ClientHandler clientHandler);
    
}
