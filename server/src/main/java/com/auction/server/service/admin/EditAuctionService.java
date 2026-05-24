package com.auction.server.service.admin;

import com.auction.server.database.AuctionListDatabase;
import com.auction.server.database.PendingAuctionDatabase;
import com.auction.server.service.auction.LockManager;
import com.auction.shared.auction.Auction;
import com.auction.shared.enums.AuctionStatus;
import com.auction.shared.request.admin.EditAuctionRequest;
import com.auction.shared.request.auction.CreateAuctionRequest;
import com.auction.shared.request.auction.PendingAuctionReviewRequest;

import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditAuctionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EditAuctionService.class);

    private static void updateAuction(CreateAuctionRequest createAuctionRequest, EditAuctionRequest editAuctionRequest){
        createAuctionRequest.setCategory(editAuctionRequest.getCategory());
        createAuctionRequest.setDescription(editAuctionRequest.getDescription());
        createAuctionRequest.setImageContent(editAuctionRequest.getImageContent());
        createAuctionRequest.setItemName(editAuctionRequest.getItemName());
    }
    private static void updateAuction(Auction auction,EditAuctionRequest editAuctionRequest){
        auction.setCategory(editAuctionRequest.getCategory());
        auction.setDescription(editAuctionRequest.getDescription());
        auction.setImageContent(editAuctionRequest.getImageContent());
        auction.setItemName(editAuctionRequest.getItemName());
        auction.setStatus(editAuctionRequest.getAuctionStatus());
    }
    // Sửa thông tin trong Dashboard trước khi duyệt/từ chối
    public static boolean editBeforeApprove(EditAuctionRequest editAuctionRequest){
        ConcurrentHashMap<Integer, PendingAuctionReviewRequest> pendingAuctionReviewList
                = PendingAuctionDatabase.getInstance().getData();
        PendingAuctionReviewRequest pendingAuctionReviewRequest
                = pendingAuctionReviewList.get(editAuctionRequest.getId());
        if (pendingAuctionReviewRequest == null) {
            LOGGER.warn(
                "Không tìm thấy pending auction để chỉnh sửa [requestId: {}]",
                editAuctionRequest.getId());
            return false;
        }
        CreateAuctionRequest createAuctionRequest
                = pendingAuctionReviewRequest.getCreateAuctionRequest();
        if (createAuctionRequest == null) {
            LOGGER.warn(
                "Pending auction không có CreateAuctionRequest [requestId: {}]",
                editAuctionRequest.getId());
            return false;
        }
        updateAuction(createAuctionRequest, editAuctionRequest);
        PendingAuctionDatabase.getInstance().saveData(pendingAuctionReviewList);
        LOGGER.info("Đã chỉnh sửa pending auction [requestId: {}]", editAuctionRequest.getId());
        return true;
    }
    //Sửa thông tin sau khi approve
    public static boolean editAfterApprove(EditAuctionRequest editAuctionRequest){
        synchronized(LockManager.getLock(editAuctionRequest.getId())){
        ConcurrentHashMap<Integer,Auction> approvedAuctionList = AuctionListDatabase.getInstance().getData();
        if (editAuctionRequest.getAuctionStatus() == AuctionStatus.CANCELLED){
            approvedAuctionList.remove(editAuctionRequest.getId());
            AuctionListDatabase.getInstance().saveData(approvedAuctionList);
            return true;
        }
        Auction auction = approvedAuctionList.get(editAuctionRequest.getId());
        if (auction == null){
            LOGGER.warn("Không tìm thấy auction để chỉnh sửa [auctionId: {}]",auction.getId());
            return false;
        }
        updateAuction(auction, editAuctionRequest);
        approvedAuctionList.put(auction.getId(), auction);
        AuctionListDatabase.getInstance().saveData(approvedAuctionList);
        LOGGER.info("Đã chỉnh sửa  auction [auctionId: {}]", editAuctionRequest.getId());
        return true;

     }
 }
}