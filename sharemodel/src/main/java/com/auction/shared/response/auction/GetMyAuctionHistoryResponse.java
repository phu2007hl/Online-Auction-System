package com.auction.shared.response.auction;

import com.auction.shared.request.auction.PendingAuctionReviewRequest;
import com.auction.shared.response.Response;
import java.util.LinkedHashMap;

/**
 * Response chứa danh sách auction đã được duyệt của user hiện tại.
 */
public class GetMyAuctionHistoryResponse extends Response {
  private final boolean valid;
  private final LinkedHashMap<Integer, PendingAuctionReviewRequest> requestList;

  /**
   * Tạo response lịch sử auction đã được duyệt.
   *
   * @param valid true nếu lấy dữ liệu thành công
   * @param requestList danh sách request đã được admin xử lý
   */
  public GetMyAuctionHistoryResponse(
      boolean valid,
      LinkedHashMap<Integer, PendingAuctionReviewRequest> requestList) {
    this.valid = valid;
    this.requestList = requestList;
  }

  /**
   * Kiểm tra request có thành công không.
   *
   * @return true nếu thành công
   */
  public boolean getResponse() {
    return valid;
  }

  /**
   * Lấy danh sách request đã được admin xử lý.
   *
   * @return danh sách request đã xử lý
   */
  public LinkedHashMap<Integer, PendingAuctionReviewRequest> getRequestList() {
    return requestList;
  }
}
