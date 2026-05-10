package com.auction.shared.response.auction;

import com.auction.shared.response.Response;

/**
* Kết quả lưu request trung gian xuống database file.
*/
public class ToDatabaseResponse extends Response {
  private final boolean valid;

  /**
  * Tạo response lưu database.
  *
  * @param valid true nếu lưu thành công
  */
  public ToDatabaseResponse(boolean valid) {
    this.valid = valid;
  }

  /**
  * Kiểm tra lưu database có thành công hay không.
  *
  * @return true nếu thành công
  */
  public boolean getResponse() {
    return valid;
  }
}
