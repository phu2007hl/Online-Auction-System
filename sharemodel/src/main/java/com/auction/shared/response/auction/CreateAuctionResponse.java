package com.auction.shared.response.auction;

import com.auction.shared.response.Response;

/**
* Kết quả tạo auction.
*/
public class CreateAuctionResponse extends Response {
  private final boolean valid;

  /**
  * Tạo response tạo auction.
  *
  * @param valid true nếu tạo auction thành công
  */
  public CreateAuctionResponse(boolean valid) {
    this.valid = valid;
  }

  /**
  * Kiểm tra tạo auction có thành công hay không.
  *
  * @return true nếu thành công
  */
  public boolean getResponse() {
    return valid;
  }
}
