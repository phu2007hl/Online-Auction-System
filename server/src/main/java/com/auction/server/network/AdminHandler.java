package com.auction.server.network;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.auction.shared.request.Request;
import com.auction.shared.response.Response;

public class AdminHandler { // thêm class riêng dành cho admin, vì t muốn request tạo auction từ luồng của client chuyển sang luồng của admin
    private Socket connection;
    private   ObjectInputStream in;
    private   ObjectOutputStream out;
    public AdminHandler(ClientHandler clienthandler){
        this.connection = clienthandler.getConnection(); //kết nối của admin vần lấy từ clientHandler. không tạo kết nối mới vì rồi
        this.in = clienthandler.getInputStream(); // luồng chuyển dữ liệu
        this.out = clienthandler.getOutputStream();
        

    }
    public void forwardRequest(Request request,ClientHandler clientHandler){
        try{
            out.writeObject(request);
             // gửi luôn cả người gửi đấu giá để sau này gửi response về vì mỗi người có những cuộc đấu giá khác nhau
            System.out.println("Forwarded succesfully");
            
        }
        catch(Exception e){
            System.out.println("Error");
            e.printStackTrace();
        }
    }
    public Response getAdminResponse(){
        try{
            Response response = (Response) in.readObject();
            return response;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
}
