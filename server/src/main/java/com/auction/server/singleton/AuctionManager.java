package com.auction.server.singleton;

public class AuctionManager{
    private static volatile AuctionManager instance;
    private AuctionManager(){}
    public static AuctionManager getInstance() {
        if (instance == null) {
            synchronized (AuctionManager.class){
                if (instance == null) {
                    instance = new AuctionManager();
                }
            }
        }
        return instance;
    }
}