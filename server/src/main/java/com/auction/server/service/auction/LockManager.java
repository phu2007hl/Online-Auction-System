package com.auction.server.service.auction; 

import java.util.HashMap;

public class LockManager {
    private static final HashMap<Integer, Object> lockMap = new HashMap<>();

    public static synchronized Object getLock(int auctionId) {
        if (!lockMap.containsKey(auctionId)) {
            Object newPadlock = new Object();
            lockMap.put(auctionId, newPadlock);
            return newPadlock;
        } else {
            return lockMap.get(auctionId);
        }
    }

    public static synchronized void clearLock(int auctionId) {
        lockMap.remove(auctionId);
    }
}
