package com.auction.server.network;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


import com.auction.server.service.LoginAuthentication;
import com.auction.server.service.RegisterAuthentication;
import com.auction.shared.model.User;
import com.auction.shared.request.LoginRequest;
import com.auction.shared.request.RegisterRequest;
import com.auction.shared.request.Request;
import com.auction.shared.response.LoginResponse;
import com.auction.shared.response.RegisterResponse;


public class ClientHandler implements Runnable {
    private Socket connection;
    private ObjectOutputStream out;
    private ObjectInputStream in;

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

                if (request instanceof LoginRequest) {
                    System.out.println("Server: building LoginResponse");
                    LoginAuthentication loginAuth = new LoginAuthentication(request);
                    LoginResponse response = (LoginResponse) loginAuth.createResponse();
                    User currentUser = loginAuth.getUserData(); //Lưu user đang trong từng luồng

                    if(currentUser!=null) System.out.println("Server: currentUse is " + currentUser.getUsername());
                    else System.out.println("Server: currentUser is null");

                    System.out.println("Server: calling responseBack()");

                    System.out.println("Server: login result = " + response.getResponse());

                    System.out.println("Server: writing response");
                    out.writeObject(response);
                    out.flush();
                    out.writeObject(currentUser);
                    out.flush();
                    System.out.println("Server: response written");
                } else if (request instanceof RegisterRequest) {
                    System.out.println("Server: building RegisterResponse");

                    RegisterAuthentication registerAuth = new RegisterAuthentication(request);
                    RegisterResponse response = (RegisterResponse) registerAuth.createResponse();
                    User currentUser = registerAuth.getUserData();

                    if(currentUser!=null) System.out.println("Server: currentUse is " + currentUser.getUsername());
                    else System.out.println("Server: currentUser is null");

                    System.out.println("Server: calling responseBack()");

                    System.out.println("Server: register result = " + response.getResponse());

                    System.out.println("Server: writing response");
                    out.writeObject(response);
                    out.flush();
                    out.writeObject(currentUser);
                    out.flush();
                    System.out.println("Server: response written");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
