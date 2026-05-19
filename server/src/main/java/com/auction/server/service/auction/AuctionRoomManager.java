package com.auction.server.service.auction;
import com.auction.server.network.ClientHandler;
import com.auction.shared.response.auction.BidUpdateResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class AuctionRoomManager {
    private static HashMap<Integer, Set<ClientHandler>> auctionRooms = new HashMap<>();
    public static boolean joinRoom(int auctionId, ClientHandler clientHandler){
        if(clientHandler == null){
            return false;
        }
        Set<ClientHandler> auctionRoom = auctionRooms.get(auctionId);
        if(auctionRoom == null){
            auctionRoom = new HashSet<>();
            auctionRooms.put(auctionId, auctionRoom);
        }
        auctionRoom.add(clientHandler);
        return true;
    }
    public static boolean leaveRoom(int auctionId, ClientHandler clientHandler) {
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
            auctionRooms.remove(auctionId);
        }
    }
}
