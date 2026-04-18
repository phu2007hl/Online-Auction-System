package com.auction.server.handler;

import com.auction.server.service.LoginAuthentication;
import com.auction.shared.model.User;
import com.auction.shared.request.Request;
import com.auction.shared.response.LoginResponse;
import com.auction.shared.response.Response;

public class LoginRequestHandler implements RequestHandler {
    @Override
    public Response handle(Request request) {
        System.out.println("Server: building LoginResponse");
        LoginAuthentication loginAuth = new LoginAuthentication(request);
        LoginResponse response = (LoginResponse) loginAuth.createResponse();
        User currentUser = loginAuth.getUserData(); //Lưu user đang trong từng luồng

        if (currentUser != null) System.out.println("Server: currentUse is " + currentUser.getUsername());
        else System.out.println("Server: currentUser is null");

        System.out.println("Server: calling responseBack()");

        System.out.println("Server: login result = " + response.getResponse());
        return response;
    }
}
