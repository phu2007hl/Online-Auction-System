package com.auction.server.handler;

import com.auction.shared.request.LoginRequest;
import com.auction.shared.request.RegisterRequest;
import com.auction.shared.request.Request;

public class RequestDispatcher {
    public static RequestHandler getHandler(Request requestType){
        if(requestType instanceof LoginRequest){
            return new LoginRequestHandler();
        }
        else if(requestType instanceof RegisterRequest){
            return new RegisterRequestHandler();
        }

        // nếu không phải loại request được xác định
        throw new IllegalArgumentException(
                "Unsupported request type: " + requestType.getClass().getSimpleName()
        );
    }
}
