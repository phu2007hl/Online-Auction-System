package com.auction.client.controller;

/**
* Lớp cơ sở cho các controller của client.
*/
public abstract class Controller {
  /**
  * Xử lý object nhận được từ server.
  *
  * @param obj object response từ server
  */
  public abstract void handle(Object obj);
}
