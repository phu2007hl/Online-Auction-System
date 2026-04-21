package com.auction.server.network;

import java.net.ServerSocket;
import java.net.Socket;

public class AuctionServer {

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(4002);
            System.out.println("Server started on port 4002...");

            while (true) {
                Socket connection = server.accept();
                System.out.println("Client connected: " + connection.getInetAddress());

                ClientHandler job = new ClientHandler(connection);
                Thread thread = new Thread(job);
                thread.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


