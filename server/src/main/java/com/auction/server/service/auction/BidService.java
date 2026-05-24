package com.auction.server.service.auction;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.auction.server.database.AuctionBidderDetailDatabase;
import com.auction.server.database.AuctionListDatabase;
import com.auction.server.model.auction.AuctionBidderDetail;
import com.auction.server.model.auction.BidProcessResult;
import com.auction.shared.auction.Auction;
import com.auction.shared.auction.BidTransaction;
import com.auction.shared.enums.AuctionStatus;
import com.auction.shared.enums.BidResponseStatus;
import com.auction.shared.enums.BidderStatus;
import com.auction.shared.model.User;
import com.auction.shared.request.auction.BidRequest;
import com.auction.shared.response.auction.BidUpdateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BidService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BidService.class);
    private BidRequest request;
    public BidService(BidRequest request) {
        this.request = request;
    }

    /**
     * Lấy ID của auction mà bidder đặt bid
     *
     * @return ID
     */
    public int getAuctionId() {
        return request.getAuctionId();
    }

    /**
     * Lấy giá bid mà người dùng đặt
     *
     * @return giá bid
     */
    public double getBidPrice() {
        return request.getBidPrice();
    }

    /**
     * Lấy người đặt bid
     *
     * @return trả object User đại diện cho người đặt bid
     */
    public User getBidder() {
        return request.getBidder();
    }

    /**
     * Hàm helper để lấy Object Auction dựa vào ID
     *
     * @return object Auction đại diện cho phiên đấu giá đó
     */
    public Auction fetchData() {
        AuctionListDatabase database = AuctionListDatabase.getInstance();
        ConcurrentHashMap<Integer, Auction> auctionList = database.getData();
        Auction auction = auctionList.get(getAuctionId());
        return auction;
    }
    public void changeAuctionDetail(Auction auction){
      LOGGER.info(
                "Đang xử lý bid [auctionId: {}, bidder: {}, bidPrice: {}, currentPrice: {}, minimumIncrement: {}]",
                getAuctionId(),
                getBidder().getEmail(),
                getBidPrice(),
                auction.getCurrentPrice(),
                auction.getMinimumIncrement());
        if (getBidPrice() >= auction.getCurrentPrice() + auction.getMinimumIncrement()) {
            //Lưu các thông tin mới của Auction vào database;
            auction.setCurrentPrice(getBidPrice());
            auction.setWinner(getBidder());
            BidTransaction bidTransaction = new BidTransaction(getBidder().getUsername(), getBidPrice());
            auction.getBidHistory().add(bidTransaction);
            ConcurrentHashMap<Integer, Auction> auctionList = AuctionListDatabase.getInstance().getData();
            AuctionListDatabase.getInstance().saveData(auctionList);
            LOGGER.info(
                    "Đã cập nhật auction sau bid thành công [auctionId: {}, winner: {}, currentPrice: {}, bidHistorySize: {}]",
                    getAuctionId(),
                    getBidder().getEmail(),
                    auction.getCurrentPrice(),
                    auction.getBidHistory().size());

    }
    }
    public void changeBidderStatus(){
            ConcurrentHashMap<Integer, AuctionBidderDetail> auctionBidderDetailHashMap = AuctionBidderDetailDatabase.
                    getInstance().getData();
            AuctionBidderDetail auctionBidderDetail = auctionBidderDetailHashMap.get(getAuctionId());
            if (auctionBidderDetail == null) {
                auctionBidderDetail = new AuctionBidderDetail(null, new HashMap<>());
                auctionBidderDetailHashMap.put(getAuctionId(), auctionBidderDetail);
                LOGGER.info("Tạo mới AuctionBidderDetail [auctionId: {}]", getAuctionId());
            }
            String currentWinnerEmail = auctionBidderDetail.getCurrentWinnerEmail();
            if (currentWinnerEmail != null) {
                auctionBidderDetail.getBidderStatusHashMap()
                        .put(currentWinnerEmail, BidderStatus.OUTBID);
                LOGGER.info(
                        "Cập nhật winner cũ thành OUTBID [auctionId: {}, oldWinner: {}]",
                        getAuctionId(),
                        currentWinnerEmail);
            }
            auctionBidderDetail.getBidderStatusHashMap().put(getBidder().getEmail(), BidderStatus.CURRENT_WINNER);
            auctionBidderDetail.setCurrentWinnerEmail(getBidder().getEmail());
            AuctionBidderDetailDatabase.getInstance().saveData(auctionBidderDetailHashMap);
            LOGGER.info(
                    "Đã cập nhật trạng thái bidder [auctionId: {}, currentWinner: {}, bidderCount: {}]",
                    getAuctionId(),
                    getBidder().getEmail(),
                    auctionBidderDetail.getBidderStatusHashMap().size());
    }

    /**
     * Hàm thực sự xử lí logic đặt bid
     * Nếu đặt thành công thì sẽ thay đổi winner và currentprice bên trong object Auction, set trạng thái
     * của winner và người bị outbid trong bidderStatus
     *
     * @return trả về một object Aucton đại diện cho thông tin của phiên đó sau khi đã cập nhật
     */
    public BidProcessResult executeLogic(int auctionId) {
        synchronized(LockManager.getLock(auctionId)){
        Auction auction = fetchData();
        if (auction == null) {
            LOGGER.warn(
                    "Từ chối bid vì không tìm thấy auction [auctionId: {}, bidder: {}, bidPrice: {}]",
                    getAuctionId(),
                    getBidder().getEmail(),
                    getBidPrice());
            return new BidProcessResult(BidResponseStatus.DECLINED, null);
        }
        if (auction.getStatus() != AuctionStatus.OPEN){
            return new BidProcessResult(BidResponseStatus.DECLINED, null);
        }
        LOGGER.info(
                "Đang xử lý bid [auctionId: {}, bidder: {}, bidPrice: {}, currentPrice: {}, minimumIncrement: {}]",
                getAuctionId(),
                getBidder().getEmail(),
                getBidPrice(),
                auction.getCurrentPrice(),
                auction.getMinimumIncrement());
        if (getBidPrice() >= auction.getCurrentPrice() + auction.getMinimumIncrement()) {
            changeAuctionDetail(auction);
            changeBidderStatus();
            return new BidProcessResult(BidResponseStatus.ACCEPTED,
                    new BidUpdateResponse(getAuctionId(),getBidPrice(),getBidder().getEmail(),
                            getBidder().getUsername(),auction.getBidHistory()));
        }
        LOGGER.info(
                "Từ chối bid vì giá không đạt bước tối thiểu [auctionId: {}, bidder: {}, bidPrice: {}, requiredPrice: {}]",
                getAuctionId(),
                getBidder().getEmail(),
                getBidPrice(),
                auction.getCurrentPrice() + auction.getMinimumIncrement());
        return new BidProcessResult(BidResponseStatus.DECLINED, null);
    }
 }
} 
