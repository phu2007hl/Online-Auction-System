package com.auction.server.service.auction;
import com.auction.server.network.ClientHandler;
import com.auction.shared.response.auction.BidUpdateResponse;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AuctionRoomManager {
    private static final ConcurrentHashMap<Integer, Set<ClientHandler>> auctionRooms =
            new ConcurrentHashMap<>();

    public static synchronized boolean joinRoom(int auctionId, ClientHandler clientHandler){
        if(clientHandler == null){
            return false;
        }
        auctionRooms.computeIfAbsent(auctionId, id -> ConcurrentHashMap.newKeySet())
                .add(clientHandler);
        // luồng hoạt động của computeIfAbsent(auctionId, id -> ConcurrentHashMap.newKeySet()) là:
        //  - coi như get(auctionId) trước
        //  + nếu k tồn tại
        //  -> truyền auctionId vào id làm key và tạo ra Set thread-safe mới làm value
        //  -> trả về dãy set vừa tạo -> sau đó add user vào

        //  + nếu đã tồn tại
        //  -> trả về dãy set value tương ứng -> add user vào
        return true;
    }

    public static synchronized boolean leaveRoom(int auctionId, ClientHandler clientHandler) {
        Set<ClientHandler> auctionRoom = auctionRooms.get(auctionId);
        if (auctionRoom == null) {
            return false;
        }
        boolean removed = auctionRoom.remove(clientHandler);
        if (auctionRoom.isEmpty()) {
            auctionRooms.remove(auctionId);
        }
        return removed;
    }

    public static void broadcast(int auctionId, BidUpdateResponse bidUpdateResponse){
        Set<ClientHandler> auctionRoom = auctionRooms.get(auctionId);
        if (auctionRoom == null || auctionRoom.isEmpty()) {
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
            }
        }
        auctionRoom.removeAll(failedClients);
        if (auctionRoom.isEmpty()) {
            auctionRooms.remove(auctionId, auctionRoom);
        }
    }
}
