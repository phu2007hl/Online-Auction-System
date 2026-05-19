package com.auction.shared.response.auction;

import com.auction.shared.response.Response;

public class JoinRoomResponse extends Response {
    private boolean success;
    private String message;

    public JoinRoomResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    @Override
    public boolean getResponse() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
