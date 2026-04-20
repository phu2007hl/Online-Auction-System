package com.auction.server.handler;

import com.auction.server.service.RegisterAuthentication;
import com.auction.shared.model.User;
import com.auction.shared.request.Request;
import com.auction.shared.response.RegisterResponse;
import com.auction.shared.response.Response;

public class RegisterRequestHandler implements RequestHandler {
    @Override
    public Response handle(Request request) {
        System.out.println("Server: building RegisterResponse");

        RegisterAuthentication registerAuth = new RegisterAuthentication(request);
        RegisterResponse response = (RegisterResponse) registerAuth.createResponse();
        User currentUser = registerAuth.getUserData();

        if (currentUser != null) System.out.println("Server: currentUse is " + currentUser.getUsername());
        else System.out.println("Server: currentUser is null");

        System.out.println("Server: calling responseBack()");

        System.out.println("Server: register result = " + response.getResponse());
        return response;
    }
}
