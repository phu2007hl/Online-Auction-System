package com.auction.client.service;

import com.auction.shared.request.RegisterRequest;
import com.auction.shared.request.Request;

public class RegisterAuthenticationService {
    private String email;
    private String password;
    private String username;
    private String errorMessage;
    public RegisterAuthenticationService(String email,String password,String username){
        this.email = email;
        this.password = password;
        this.username = username;
    }
    public String getEmail(){
        return email;
    }
    public String getPassword(){
        return password;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean getEmailAuthentication() {
        if (!email.contains("@gmail.com")) {
            return false;
        }

        String beforeCriteria = email.substring(0, email.indexOf("@")); // trước @gmail.com phải có xâu
        return beforeCriteria.matches(".*[a-zA-Z].*");
    }
    public boolean getPasswordAuthentication(){
        boolean validLength = password.length() >= 8;
        boolean hasLetter = password.matches(".*[a-zA-Z].*");  // có chữ cái
        boolean hasDigit = password.matches(".*\\d.*");         // có số (0-9)
        return validLength && hasLetter && hasDigit;
    }
    public Request createAuthRequest(){
        if (getEmailAuthentication() == false){
            errorMessage = "Email không hợp lệ (Phải có dạng abc@gmail.com)";
            return null;
        }
        if (getPasswordAuthentication() == false){
            errorMessage = "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ cái và số";
            return null;
        }
        return new RegisterRequest(email, password, username);
    }

}
