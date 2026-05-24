package com.auction.server.service.auction;

import com.auction.server.database.AuctionListDatabase;
import com.auction.shared.auction.Auction;
import com.auction.shared.enums.AuctionStatus;

import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.auction.shared.response.auction.AuctionAutoCloseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuctionClosingScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuctionClosingScheduler.class);
    private static final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();
    private static boolean started;

    public static void start() {
        if (started) {
            return;
        }
        started = true;
        scheduler.scheduleAtFixedRate(
                () -> {
                    try {
                        closeExpiredAuctions();
                    } catch (Exception e) {
                        LOGGER.error("Lỗi khi tự động đóng auction quá hạn", e);
                    }
                },
                0,
                30,
                TimeUnit.SECONDS
        );
        LOGGER.info("Đã khởi động AuctionClosingScheduler");
    }

    private static void closeExpiredAuctions(){
        ConcurrentHashMap<Integer, Auction> auctionList = AuctionListDatabase.getInstance().getData();
        boolean changed = false;
        LocalDate today = LocalDate.now();

        for (Integer auctionId : auctionList.keySet()){
            Auction auction = auctionList.get(auctionId);
            if (auction == null) {
                continue;
            }

            synchronized (LockManager.getLock(auctionId)) {
                if (auction.getStatus() != AuctionStatus.OPEN) {
                    continue;
                }
                if (!auction.getEndTime().isBefore(today)) {
                    continue;
                }

                auction.setStatus(AuctionStatus.CLOSED);
                AuctionRoomManager.broadcast(auctionId, new AuctionAutoCloseResponse(true));
                changed = true;
                LOGGER.info("Đã tự động đóng auction quá hạn [auctionId: {}]", auctionId);
            }
        }

        if (changed) {
            AuctionListDatabase.getInstance().saveData(auctionList);
        }
    }
}
