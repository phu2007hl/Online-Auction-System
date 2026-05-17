package com.auction.server.handler.auction;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.auction.server.database.AuctionDetailDatabase;
import com.auction.server.handler.RequestHandler;
import com.auction.server.network.ClientHandler;
import com.auction.server.service.auction.BidService;
import com.auction.shared.auction.Auction;
import com.auction.shared.enums.BidRequestStatus;
import com.auction.shared.enums.ParticipantStatus;
import com.auction.shared.model.User;
import com.auction.shared.request.Request;
import com.auction.shared.request.auction.BidRequest;
import com.auction.shared.response.Response;
import com.auction.shared.response.auction.ParticipantResponse;

public class BidRequestHandler implements RequestHandler {
    public Response handle(Request request,ClientHandler clientHandler){
        BidRequest req = (BidRequest) request; //Downcast request
        BidService service = new BidService(req); //Gọi service để thực thi logic
        Auction auction = service.executeLogic(); //Thực thi logic
        //Sau khi thực thi logic thì status của request đã được thay thế, từ đây kiểm tra
        if (req.getBidRequestStatus().equals(BidRequestStatus.ACCEPTED)){ //Nếu status là ACCEPTED sẽ làm hai việc dưới
            broadcast(req, auction, service.getoutBidUser()); //Broadcast các dữ liệu mới của phiên cho các người trong phiên
            return new ParticipantResponse(auction.getCurrentPrice(), auction.getBidHistory(),ParticipantStatus.WINNER); //trả về cho người đặt bid rằng họ đang là winner hiện tại
        }
        else{ //Nếu status là DECLINED
            return new ParticipantResponse(auction.getCurrentPrice(), auction.getBidHistory(),ParticipantStatus.DECLINEDBID); //trả về cho người đặt bid rằng yêu cầu đặt bid của họ đã bị từ chối
        

        }


        }
        
    /**
     * Hàm helper dùng để brroadcast về cho những người trong phiên
     * @param request nhận bidRequest
     * @param auction nhận phiên đã cập nhật sau khi xử lí logic
     * @param outBidUser nhận user bị outbid để còn biết mà gửi về
     */
    public void broadcast(BidRequest request,Auction auction,User outBidUser){
        ConcurrentHashMap<Integer,ArrayList<ClientHandler>> currentParticipant = AuctionDetailDatabase.getInstance().getData();
        
        if (request.getBidRequestStatus().equals(BidRequestStatus.ACCEPTED)){
            for (ClientHandler connection:currentParticipant.get(request.getAuctionId())){
                if (connection.getUser().equals(outBidUser)){
                    try{
                        connection.getOutputStream().writeObject(new ParticipantResponse(auction.getCurrentPrice(),auction.getBidHistory(), ParticipantStatus.OUTBID));
                    }
                    catch (Exception e){
                        ////
                        /// 
                        e.printStackTrace();

                    }
                    

                }
                else if (!connection.getUser().equals(request.getBidder())){
                    try{
                        connection.getOutputStream().writeObject(new ParticipantResponse(auction.getCurrentPrice(), auction.getBidHistory(), ParticipantStatus.VIEWER));

                    }
                    catch (Exception e){
                        ////
                        /// 
                        e.printStackTrace();
                    }
                }
            }
        }

    }
    
}
