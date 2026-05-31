package com.auction.server.service.auction;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auction.server.database.PendingAuctionDatabase;
import com.auction.shared.request.auction.PendingAuctionReviewRequest;
/**
 * Tạo ra một lớp riêng để xử lí logic xoá request chờ
 */
public class RemoveRequestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveRequestService.class);
    public static void removeRequest(int id){
        PendingAuctionDatabase database = PendingAuctionDatabase.getInstance();
        synchronized (database) {
            ConcurrentHashMap<Integer, PendingAuctionReviewRequest> requestList = database.getData();
            requestList.remove(id);
            LOGGER.debug("Đã xoá request với id - {} ra khỏi danh sách chờ duyệt", id);
            database.saveData(requestList);
        }
    }
}
