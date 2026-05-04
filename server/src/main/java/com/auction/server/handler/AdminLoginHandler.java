package com.auction.server.handler;

import com.auction.server.network.AdminHandler;
import com.auction.server.network.ClientHandler;
import com.auction.server.service.AdminLoginAuthentication;
import com.auction.shared.request.AdminLoginRequest;
import com.auction.shared.request.Request;
import com.auction.shared.response.AdminLoginResponse;
import com.auction.shared.response.Response;

public class AdminLoginHandler implements RequestHandler{
    public Response handle(Request request, ClientHandler clienthandler){
        AdminLoginAuthentication adminAuth = new AdminLoginAuthentication(request);
        AdminLoginResponse response =(AdminLoginResponse) adminAuth.createResponse();
        if (response.getResponse()){
            AdminHandler adminhandler = new AdminHandler(clienthandler);
            ClientHandler.setAdminHandler(adminhandler);
            System.out.println("Admin connected");
        }
        return response;
        
    }

}