package com.auction.shared.response.admin;

import com.auction.shared.request.auction.PendingAuctionReviewRequest;
import com.auction.shared.response.Response;
import java.util.concurrent.ConcurrentHashMap;

/**
* Kết quả lấy danh sách auction đang chờ duyệt.
*/
public class GetPendingAuctionListResponse extends Response {
  private final ConcurrentHashMap<Integer, PendingAuctionReviewRequest> requestList;
  private final boolean valid;

  /**
  * Tạo response danh sách pending auction.
  *
  * @param valid true nếu lấy dữ liệu thành công
  * @param requestList danh sách auction đang chờ duyệt
  */
  public GetPendingAuctionListResponse(
      boolean valid,
      ConcurrentHashMap<Integer, PendingAuctionReviewRequest> requestList) {
    this.requestList = requestList;
    this.valid = valid;
  }

  /**
  * Kiểm tra lấy danh sách có thành công hay không.
  *
  * @return true nếu thành công
  */
  public boolean getResponse() {
    return valid;
  }

  /**
  * Lấy danh sách auction đang chờ duyệt.
  *
  * @return danh sách pending auction
  */
  public ConcurrentHashMap<Integer, PendingAuctionReviewRequest> getList() {
    return requestList;
  }
}
