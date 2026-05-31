package com.auction.shared.request.auction;

import com.auction.shared.enums.CreateAuctionStatus;
import com.auction.shared.request.Request;

/**
 * Request xử lý toàn bộ kết quả duyệt auction của admin.
 */
public class ProcessAuctionReviewRequest extends Request {
  private final PendingAuctionReviewRequest pendingRequest;
  private final CreateAuctionStatus status;

  /**
   * Tạo request xử lý kết quả duyệt auction.
   *
   * @param pendingRequest request đang chờ duyệt
   * @param status kết quả duyệt
   */
  public ProcessAuctionReviewRequest(
      PendingAuctionReviewRequest pendingRequest,
      CreateAuctionStatus status) {
    this.pendingRequest = pendingRequest;
    this.status = status;
  }

  /**
   * Lấy request đang chờ duyệt.
   *
   * @return request đang chờ duyệt
   */
  public PendingAuctionReviewRequest getPendingRequest() {
    return pendingRequest;
  }

  /**
   * Lấy kết quả duyệt.
   *
   * @return trạng thái duyệt
   */
  public CreateAuctionStatus getStatus() {
    return status;
  }
}
