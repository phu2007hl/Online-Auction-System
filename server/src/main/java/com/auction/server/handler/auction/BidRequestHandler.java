package com.auction.server.handler.auction;

import com.auction.server.handler.RequestHandler;
import com.auction.server.model.auction.BidProcessResult;
import com.auction.server.network.ClientHandler;
import com.auction.server.service.auction.AuctionRoomManager;
import com.auction.server.service.auction.BidService;
import com.auction.shared.auction.Auction;
import com.auction.shared.enums.BidResponseStatus;
import com.auction.shared.model.User;
import com.auction.shared.request.Request;
import com.auction.shared.request.auction.BidRequest;
import com.auction.shared.response.Response;
import com.auction.shared.response.auction.BidResultResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BidRequestHandler implements RequestHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(BidRequestHandler.class);

    public Response handle(Request request, ClientHandler clientHandler) {
        BidRequest req = (BidRequest) request; //Downcast request
        LOGGER.info(
                "Nhận bid request [auctionId: {}, bidder: {}, bidPrice: {}]",
                req.getAuctionId(),
                req.getBidder().getEmail(),
                req.getBidPrice());
        BidService service = new BidService(req); //Gọi service để thực thi logic
        BidProcessResult bidProcessResult = service.executeLogic(req.getAuctionId()); //Thực thi logic
        //Sau khi thực thi logic thì status của request đã được thay thế, từ đây kiểm tra
        if(bidProcessResult.getBidResponseStatus() == BidResponseStatus.ACCEPTED)
        {
            LOGGER.info(
                    "Bid request được chấp nhận [auctionId: {}, bidder: {}]",
                    req.getAuctionId(),
                    req.getBidder().getEmail());
            AuctionRoomManager.broadcast(req.getAuctionId(),bidProcessResult.getBidUpdateResponse());
            return new BidResultResponse(BidResponseStatus.ACCEPTED, "Đặt bid thành công");
        }
        else{
            LOGGER.info(
                    "Bid request bị từ chối [auctionId: {}, bidder: {}, bidPrice: {}]",
                    req.getAuctionId(),
                    req.getBidder().getEmail(),
                    req.getBidPrice());
            return new BidResultResponse(BidResponseStatus.DECLINED, "Đặt bid thất bại: " +
                    "Bid phải lớn hơn hoặc bằng giá hiện tại + bước giá tối thiểu");
        }
    }
}
