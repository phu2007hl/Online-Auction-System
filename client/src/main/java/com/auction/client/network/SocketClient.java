package com.auction.client.network;

import com.auction.client.controller.Controller;
import com.auction.shared.request.Request;

import javafx.application.Platform;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;

public class SocketClient {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Controller controller;

    private Thread listenerThread;
    private volatile boolean listening = false;

    public SocketClient(int port) {
        try {
            socket = new Socket("localhost", port);
            System.out.println("Connected to server");

            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();

            in = new ObjectInputStream(socket.getInputStream());

        } catch (Exception e) {
            System.out.println("Error");
            e.printStackTrace();
        }
    }
    public void setController(Controller controller){
        this.controller = controller;
    }

    public synchronized void sendRequest(Request request) {
        try {
            System.out.println("Client: writing request");
            out.writeObject(request);
            out.flush();
            out.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public synchronized void startListening() {
        if (listenerThread != null && listenerThread.isAlive()) {
            return;
        }

        listening = true;

        listenerThread = new Thread(() -> {
            while (listening) {
                try {
                    Object obj = in.readObject();
                    
                    System.out.println("Response received");

                    Platform.runLater(()->controller.handle(obj));
                    System.out.println("Response handled");

                } catch (Exception e) {
                    e.printStackTrace();
                    listening = false;
                    break;
                }
            }
        });

        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public void stopListening() {
        listening = false;
    }

    public ObjectInputStream getInStream() {
        return in;
    }

    public ObjectOutputStream getOutStream() {
        return out;
    }
    public void setSocket(){
        this.socket = socket;
    }
}

