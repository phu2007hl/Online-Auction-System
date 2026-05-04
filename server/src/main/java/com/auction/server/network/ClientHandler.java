package com.auction.server.network;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import com.auction.server.handler.RequestDispatcher;
import com.auction.server.handler.RequestHandler;
import com.auction.shared.model.User;
import com.auction.shared.request.Request;
import com.auction.shared.response.Response;


public class ClientHandler implements Runnable {
    private Socket connection;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private RequestDispatcher dispatcher = new RequestDispatcher();
    private static AdminHandler adminHandler;
    private User user;
    private static HashMap<User,ClientHandler> responseAdmin; 
    private static ArrayList<ClientHandler> onlineUser = new ArrayList<>();

    public ClientHandler(Socket connection) {
        this.connection = connection;
    }

    public void run() {
        try {
             out = new ObjectOutputStream(connection.getOutputStream());
            out.flush();
            System.out.println("Server: output stream ready");

             in = new ObjectInputStream(connection.getInputStream());
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
                Response response = handler.handle(request,this);

                System.out.println("Server: writing response");
                out.writeObject(response);
                out.flush();
                System.out.println("Server: response written");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public ObjectOutputStream getOutputStream() {
        return out;
    }
    public ObjectInputStream getInputStream() {
        return in;
    }
    public Socket getConnection() {
        return connection;
    }
    public static AdminHandler getAdminHandler() {
        return adminHandler;
    }
    public static void setAdminHandler(AdminHandler admin){
        adminHandler = admin;

    }
    public void setUser(User user){
        this.user = user;
    }
    public User getUser(){
        return user;
    }
    public static void addOnlineUser(ClientHandler user){
        onlineUser.add(user);
    }
    public static void removeOfflineUser(ClientHandler user){
        onlineUser.remove(user);
    }
    public static ArrayList<ClientHandler> getOnlineUser(){
        return onlineUser;
    }
    public static void addRequest(User user, ClientHandler clientHandler){
        responseAdmin.put(user, clientHandler);
    }
    public static void removeRequest(User user){
        responseAdmin.remove(user);
    }
    public static HashMap<User,ClientHandler> getMap(){
        return responseAdmin;
    }
    

}
