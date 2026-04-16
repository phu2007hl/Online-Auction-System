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
        String criteria = "@gmail.com";
        if (!email.contains(criteria)) {
            return false;
        }// phải có @gmail.com

        String beforeCriteria = email.substring(0, email.indexOf("@")); // trước @gmail.com phải có xâu
        boolean beforeCriteriaExists = beforeCriteria.matches(".*[a-zA-Z].*");

        return beforeCriteriaExists;
    }

    public Request createAuthRequest() {
        if (getEmailAuthentication() == false) {
            errorMessage = "Email không hợp lệ";
            return null;
        }
        return new LoginRequest(email, password);
    }
}

