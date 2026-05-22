package com.auction.server.service.auction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auction.server.network.ClientHandler;
import com.auction.shared.auction.Auction;

public class BroadcastApprovedAuction {
    private static final Logger LOGGER = LoggerFactory.getLogger(BroadcastApprovedAuction.class);
    public static void broadcast(Auction auction,ClientHandler clientHandler){
      for (ClientHandler user : ClientHandler.getOnlineUser()) {
      try {
        user.getOutputStream().writeObject(auction);
      } catch (Exception e) {
        LOGGER.error("Không thể đẩy auction đã duyệt tới client online", e);
        continue;
      }
    } 
    }
    
}
