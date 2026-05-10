package com.auction.shared.response;

import java.io.Serializable;

/**
* Lớp cơ sở cho mọi response gửi giữa client và server.
*/
public abstract class Response implements Serializable {
  /**
  * Lấy trạng thái xử lý của response.
  *
  * @return true nếu xử lý thành công
  */
  public abstract boolean getResponse();
}
