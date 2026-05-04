package com.auction.server.handler;

import com.auction.server.network.ClientHandler;
import com.auction.shared.request.AdminLoginRequest;
import com.auction.shared.request.LoadAuctionRequest;
import com.auction.shared.request.LogOutRequest;
import com.auction.shared.request.LoginRequest;
import com.auction.shared.request.RegisterRequest;
import com.auction.shared.request.Request;
import com.auction.shared.request.ToDatabaseRequest;
import com.auction.shared.request.UpdateMainPageRequest;
import com.auction.shared.request.UpdateUserRequest;
import com.auction.shared.request.createAuctionRequest;
import com.auction.shared.request.getAuctionListRequest;

public class RequestDispatcher {
    public static RequestHandler getHandler(Request requestType){
        if(requestType instanceof LoginRequest){
            return new LoginRequestHandler();
        }
        else if(requestType instanceof RegisterRequest){
            return new RegisterRequestHandler();
        }
        else if (requestType instanceof AdminLoginRequest){
            return new AdminLoginHandler();
        }
        else if (requestType instanceof createAuctionRequest){
            return new createAuctionHandler();
        }
        else if (requestType instanceof getAuctionListRequest){
            return new getAuctionListHandler();
        }
        else if (requestType instanceof LoadAuctionRequest){
            return new LoadAuctionHandler();
        }
        else if (requestType instanceof LogOutRequest){
            return new LogOutHandler();
        }
        else if (requestType instanceof ToDatabaseRequest){
            return new ToDatabaseHandler();
        }
        else if (requestType instanceof UpdateMainPageRequest){
            return new UpdateMainPageHandler();  
        }
        else if (requestType instanceof UpdateUserRequest){
            return new UpdateUserRequestHandler();
        }

        // nếu không phải loại request được xác định
        throw new IllegalArgumentException(
                "Unsupported request type: " + requestType.getClass().getSimpleName()
        );
    }
}
