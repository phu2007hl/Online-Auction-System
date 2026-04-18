package com.auction.server.handler;

import com.auction.shared.request.Request;
import com.auction.shared.response.Response;

public interface RequestHandler {
    Response handle(Request request);
}
