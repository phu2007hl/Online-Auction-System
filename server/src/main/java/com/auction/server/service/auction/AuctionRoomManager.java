package com.auction.server.service.auction;
import com.auction.server.network.ClientHandler;
import com.auction.shared.response.auction.BidUpdateResponse;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuctionRoomManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuctionRoomManager.class);
    private static final ConcurrentHashMap<Integer, Set<ClientHandler>> auctionRooms =
            new ConcurrentHashMap<>();

    public static synchronized boolean joinRoom(int auctionId, ClientHandler clientHandler){
        if(clientHandler == null){
            LOGGER.warn("Không thể join room vì clientHandler null [auctionId: {}]", auctionId);
            return false;
        }
        auctionRooms.computeIfAbsent(auctionId, id -> ConcurrentHashMap.newKeySet())
                .add(clientHandler);
        LOGGER.info(
                "Client đã join auction room [auctionId: {}, roomSize: {}]",
                auctionId,
                auctionRooms.get(auctionId).size());
        return true;
    }

    public static synchronized boolean leaveRoom(int auctionId, ClientHandler clientHandler) {
        Set<ClientHandler> auctionRoom = auctionRooms.get(auctionId);
        if (auctionRoom == null) {
            LOGGER.warn("Không thể leave room vì room không tồn tại [auctionId: {}]", auctionId);
            return false;
        }
        boolean removed = auctionRoom.remove(clientHandler);
        if (auctionRoom.isEmpty()) {
            auctionRooms.remove(auctionId);
            LOGGER.info("Đã xóa auction room rỗng [auctionId: {}]", auctionId);
        }
        LOGGER.info(
                "Client leave auction room [auctionId: {}, removed: {}, roomSize: {}]",
                auctionId,
                removed,
                auctionRoom.size());
        return removed;
    }

    public static void broadcast(int auctionId, BidUpdateResponse bidUpdateResponse){
        Set<ClientHandler> auctionRoom = auctionRooms.get(auctionId);
        if (auctionRoom == null || auctionRoom.isEmpty()) {
            LOGGER.info("Bỏ qua broadcast vì auction room rỗng [auctionId: {}]", auctionId);
            return;
        }
        Set<ClientHandler> failedClients = new HashSet<>();
        for(ClientHandler clientHandler : auctionRoom){
            try {
                clientHandler.getOutputStream().writeObject(bidUpdateResponse);
                clientHandler.getOutputStream().flush();
                clientHandler.getOutputStream().reset();
            } catch (IOException e) {
                failedClients.add(clientHandler);
                LOGGER.warn("Không thể broadcast bid update tới một client [auctionId: {}]", auctionId, e);
            }
        }
        auctionRoom.removeAll(failedClients);
        if (auctionRoom.isEmpty()) {
            auctionRooms.remove(auctionId, auctionRoom);
        }
        LOGGER.info(
                "Đã broadcast bid update [auctionId: {}, successCount: {}, failedCount: {}]",
                auctionId,
                auctionRoom.size(),
                failedClients.size());
    }
}
