package com.auction.server.handler.auction;

import com.auction.server.database.AuctionBidderDetailDatabase;
import com.auction.server.database.AuctionListDatabase;
import com.auction.server.handler.RequestHandler;
import com.auction.server.model.auction.AuctionBidderDetail;
import com.auction.server.network.ClientHandler;
import com.auction.server.service.auction.LockManager;
import com.auction.shared.auction.Auction;
import com.auction.shared.enums.AuctionStatus;
import com.auction.shared.enums.BidderStatus;
import com.auction.shared.request.Request;
import com.auction.shared.request.auction.GetAuctionDetailRequest;
import com.auction.shared.response.Response;
import com.auction.shared.response.auction.GetAuctionDetailResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class GetAuctionDetailRequestHandler implements RequestHandler {
    @Override
    public Response handle(Request request, ClientHandler clientHandler) {
        GetAuctionDetailRequest getAuctionDetailRequest = (GetAuctionDetailRequest) request;
        ConcurrentHashMap<Integer, Auction> auctionList = AuctionListDatabase.getInstance().getData();
        Auction auction = auctionList.get(getAuctionDetailRequest.getAuctionId());

        if (auction == null || clientHandler.getUser() == null) {
            return new GetAuctionDetailResponse(false, auction, null, null);
        }

        String currentUserEmail = clientHandler.getUser().getEmail();
        AuctionStatus auctionStatus = auction.getStatus();

        synchronized (LockManager.getLock(auction.getId())) {
            if (auction.getStatus() == AuctionStatus.OPEN
                    && auction.getEndTime() != null
                    && auction.getEndTime().isBefore(LocalDateTime.now())) {
                auctionStatus = AuctionStatus.CLOSED;
                auction.setStatus(AuctionStatus.CLOSED);
                AuctionListDatabase.getInstance().saveData(auctionList);
            } else {
                auctionStatus = auction.getStatus();
            }
        }

        BidderStatus bidderStatus = getBidderStatus(
                getAuctionDetailRequest.getAuctionId(),
                currentUserEmail,
                auction);

        return new GetAuctionDetailResponse(true, auction, auctionStatus, bidderStatus);
    }

    private BidderStatus getBidderStatus(int auctionId, String currentUserEmail, Auction auction) {
        if (auction.getSeller().getEmail().equals(currentUserEmail)) {
            return BidderStatus.VIEWER_ONLY;
        }

        ConcurrentHashMap<Integer, AuctionBidderDetail> auctionBidderDetailList =
                AuctionBidderDetailDatabase.getInstance().getData();
        AuctionBidderDetail auctionBidderDetail = auctionBidderDetailList.get(auctionId);

        if (auctionBidderDetail == null || auctionBidderDetail.getBidderStatusHashMap() == null) {
            return BidderStatus.VIEWER;
        }

        HashMap<String, BidderStatus> bidderStatusHashMap =
                auctionBidderDetail.getBidderStatusHashMap();
        BidderStatus bidderStatus = bidderStatusHashMap.get(currentUserEmail);

        if (bidderStatus == null) {
            return BidderStatus.VIEWER;
        }

        return bidderStatus;
    }
}
