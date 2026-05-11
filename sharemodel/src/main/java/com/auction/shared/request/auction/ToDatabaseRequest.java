package com.auction.shared.request.auction;

import com.auction.shared.request.Request;

/**
* Yêu cầu lưu một request trung gian xuống database file.
*/
public class ToDatabaseRequest extends Request {
  private final Request request;

  /**
  * Tạo request lưu dữ liệu xuống database.
  *
  * @param request request cần lưu
  */
  public ToDatabaseRequest(Request request) {
    this.request = request;
  }

  /**
  * Lấy request cần lưu xuống database.
  *
  * @return request cần lưu
  */
  public Request getRequest() {
    return request;
  }
}
