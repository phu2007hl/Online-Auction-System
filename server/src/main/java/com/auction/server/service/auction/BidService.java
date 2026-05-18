package com.auction.server.service.auction;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.auction.server.database.AuctionListDatabase;
import com.auction.server.database.UserBidStatusDatabase;
import com.auction.shared.auction.Auction;
import com.auction.shared.auction.BidTransaction;
import com.auction.shared.enums.BidRequestStatus;
import com.auction.shared.enums.ParticipantStatus;
import com.auction.shared.model.User;
import com.auction.shared.request.auction.BidRequest;

public class BidService {
    private BidRequest request;
    private User outBidUser;
    public BidService(BidRequest request){
        this.request = request;
    }
    /**
     * Lấy ID của auction mà bidder đặt bid
     * @return ID
     */
    public int getAuctionId(){
        return request.getAuctionId();
    }
    /**
     * Lấy giá bid mà người dùng đặt
     * @return giá bid
     */
    public double getBidPrice(){
        return request.getBidPrice();
    }
    /**
     * Lấy người đặt bid
     * @return trả object User đại diện cho người đặt bid
     */
    public User getBidder(){
        return request.getBidder();
    }
    /**
     * Hàm helper để lấy Object Auction dựa vào ID
     * @return object Auction đại diện cho phiên đấu giá đó
     */
    public Auction fetchData(){
        AuctionListDatabase database = AuctionListDatabase.getInstance();
        ConcurrentHashMap<Integer,Auction> auctionList = database.getData();
        Auction auction = auctionList.get(getAuctionId());
        return auction;
    }
    /**
     * Lấy trạng thái của các các bidder đã đặt bid
     * @return
     */
    public ConcurrentHashMap<Integer,HashMap<User,ParticipantStatus>> fetchBidderData(){
        UserBidStatusDatabase database = UserBidStatusDatabase.getInstance();
        ConcurrentHashMap<Integer,HashMap<User,ParticipantStatus>> bidderStatus = database.getData();
        return bidderStatus;
    }
    /**
     * Lấy người dùng bị outbid
     * @return trả về một Object User đại diện cho người bị outbid
     */
    public User getoutBidUser(){
        return outBidUser;
    }
    /**
     * Hàm thực sự xử lí logic đặt bid
     * Nếu đặt thành công thì sẽ thay đổi winner và currentprice bên trong object Auction, set trạng thái
     * của winner và người bị outbid trong bidderStatus
     * @return trả về một object Aucton đại diện cho thông tin của phiên đó sau khi đã cập nhật
     */
    public Auction executeLogic(){
        Auction auction = fetchData();
        if (getBidPrice() > auction.getCurrentPrice()){
            auction.setCurrentPrice(getBidPrice()); //thay đổi currentPrice
            auction.getBidHistory().add(new BidTransaction(getAuctionId(), getBidder(), getBidPrice(), getAuctionId())); //Thêm vào lịch sử bid
            auction.setWinner(getBidder()); //Thay đổi winner
            request.setStatus(BidRequestStatus.ACCEPTED); //Thay đổi trạng thái của request đó sang ACCEPTED, trạng thái request gồm Accepted vad Declined
            ConcurrentHashMap<Integer,HashMap<User,ParticipantStatus>> bidderstatus = fetchBidderData();
            bidderstatus.get(getAuctionId()).put(getBidder(), ParticipantStatus.WINNER); //Thay đổi trạng thái ở bên trong UserBidStatusDatabase
            if (auction.getBidHistory().size() >= 2){ //Kiểm tra xem lịch sử bid có lớn hơn 2 không trước khi lấy người bị outbid, tránh lỗi Index.
                User outBidUser = auction.getBidHistory().get(-2).getBidder(); //Lấy người bị Outbid
                this.outBidUser = outBidUser; // Đặt như này để sau dễ lấy trong handler.
                
                bidderstatus.get(getAuctionId()).put(outBidUser, ParticipantStatus.OUTBID); // Thay đổi trạng thái của người bị outbid trong UserBidStatusDatabase


            }
            return auction;
            

        }
        else{
            request.setStatus(BidRequestStatus.DECLINED);
            return auction;
        }



    } 

    
}
