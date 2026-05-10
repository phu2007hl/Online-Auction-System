package com.auction.shared.response.auction;

import com.auction.shared.request.Request;
import com.auction.shared.response.Response;
import java.util.ArrayList;

/**
* Kết quả lấy danh sách auction đã được duyệt.
*/
public class GetApprovedAuctionListResponse extends Response {
  private final boolean valid;
  private final ArrayList<Request> auctionList;

  /**
  * Tạo response danh sách auction đã duyệt.
  *
  * @param valid true nếu lấy dữ liệu thành công
  * @param auctionList danh sách auction đã duyệt
  */
  public GetApprovedAuctionListResponse(boolean valid, ArrayList<Request> auctionList) {
    this.valid = valid;
    this.auctionList = auctionList;
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
  * Lấy danh sách auction đã duyệt.
  *
  * @return danh sách auction
  */
  public ArrayList<Request> getAuctionList() {
    return auctionList;
  }
}
