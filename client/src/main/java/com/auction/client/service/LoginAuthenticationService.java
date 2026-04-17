package com.auction.client.service;

import com.auction.shared.request.LoginRequest;
import com.auction.shared.request.Request;

public class LoginAuthenticationService {
    private String email;
    private String password;
    private String errorMessage;

    public LoginAuthenticationService(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean getEmailAuthentication() {
        if (!email.contains("@gmail.com") || !email.endsWith("@gmail.com")) {
            return false;
        }
        String beforeAt = email.substring(0, email.indexOf("@"));
        return !beforeAt.isEmpty();
    }

    public Request createAuthRequest() {
        if (getEmailAuthentication() == false) {
            errorMessage = "Email không hợp lệ";
            return null;
        }
        return new LoginRequest(email, password);
    }
}

