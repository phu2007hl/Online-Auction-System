package com.auction.shared.response.auction;

import com.auction.shared.response.Response;

public class LeaveRoomResponse extends Response {
    private boolean success;
    private String message;

    public LeaveRoomResponse(boolean success, String message) {
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
