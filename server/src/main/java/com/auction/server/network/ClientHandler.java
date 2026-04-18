package com.auction.server.network;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


import com.auction.server.handler.RequestDispatcher;
import com.auction.server.handler.RequestHandler;
import com.auction.shared.request.Request;
import com.auction.shared.response.Response;


public class ClientHandler implements Runnable {
    private Socket connection;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private RequestDispatcher dispatcher = new RequestDispatcher();

    public ClientHandler(Socket connection) {
        this.connection = connection;
    }

    public void run() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
            out.flush();
            System.out.println("Server: output stream ready");

            ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
            System.out.println("Server: input stream ready");

            while (true) {
                System.out.println("Server: waiting for request");
                Request request = (Request) in.readObject();
                System.out.println("Server: request received");

                if (request == null) {
                    System.out.println("Server: request is null");
                    continue;
                }

                System.out.println("Server: request type = " + request.getClass().getSimpleName());

                // tùy vào loại request mà RequestDispatcher sẽ chọn handler thích hợp để xử lý request
                RequestHandler handler = dispatcher.getHandler(request);
                Response response = handler.handle(request);

                System.out.println("Server: writing response");
                out.writeObject(response);
                out.flush();
                System.out.println("Server: response written");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
