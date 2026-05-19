package com.auction.server.service.auction;

import java.util.concurrent.ConcurrentHashMap;

public class AuctionLockManager {
    private static final ConcurrentHashMap<Integer, Object> auctionLocks =
            new ConcurrentHashMap<>();

    private AuctionLockManager() {
    }

    public static Object getLock(int auctionId) {
        return auctionLocks.computeIfAbsent(auctionId, id -> new Object());
    }
}
