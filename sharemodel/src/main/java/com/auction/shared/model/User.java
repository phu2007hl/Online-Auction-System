package com.auction.shared.model;

import java.io.Serializable;

/**
* Model người dùng chứa thông tin đăng nhập và đăng ký.
*/
public class User implements Serializable {
  private String email;
  private String password;
  private String username;

  /**
  * Tạo user với email, mật khẩu và tên đăng nhập.
  *
  * @param email email của user
  * @param password mật khẩu của user
  * @param username tên đăng nhập của user
  */
  public User(String email, String password, String username) {
    this.email = email;
    this.password = password;
    this.username = username;
  }

  /**
  * Lấy email của user.
  *
  * @return email
  */
  public String getEmail() {
    return email;
  }

  /**
  * Lấy mật khẩu của user.
  *
  * @return mật khẩu
  */
  public String getPassword() {
    return password;
  }

  /**
  * Lấy tên đăng nhập của user.
  *
  * @return tên đăng nhập
  */
  public String getUsername() {
    return username;
  }
}
