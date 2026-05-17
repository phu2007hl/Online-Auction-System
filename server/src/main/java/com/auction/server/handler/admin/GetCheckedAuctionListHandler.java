package com.auction.server.handler.admin;

import com.auction.server.database.AdminResponseDatabase;
import com.auction.server.handler.RequestHandler;
import com.auction.server.network.ClientHandler;
import com.auction.shared.request.Request;
import com.auction.shared.request.auction.PendingAuctionReviewRequest;
import com.auction.shared.response.Response;
import com.auction.shared.response.admin.GetCheckedAuctionListResponse;

import java.util.LinkedHashMap;

public class GetCheckedAuctionListHandler implements RequestHandler {
    @Override
    public Response handle(Request request, ClientHandler clientHandler) {
        LinkedHashMap<Integer, PendingAuctionReviewRequest> checkedAuctionList
                = AdminResponseDatabase.getInstance().getData();
        return new GetCheckedAuctionListResponse(true, checkedAuctionList);
    }
}
