package com.auction.client.network;

import com.auction.shared.request.Request;
import com.auction.shared.response.Response;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public SocketClient() {
        try {
            socket = new Socket("localhost", 4000);
            System.out.println("Connected to server");

            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();

            in = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            System.out.println("Error");
            e.printStackTrace();
        }
    }

    public  Response sendRequest(Request request) {
        try {
            System.out.println("Client: writing request");
            out.writeObject(request);
            out.flush();
            System.out.println("Client: waiting for response");

            Response response =  (Response) in.readObject();
            System.out.println("Client: response received");
            return response;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

