package com.base.CRM.exception;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

public class RestErrorClass {

  private HttpStatus status;
  private String message;
  private LocalDateTime timeStamp;

  public RestErrorClass(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
    this.timeStamp = LocalDateTime.now();
  }

  public HttpStatus getStatus() {
    return status;
  }

  public void setStatus(HttpStatus status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public LocalDateTime getTimeStamp() {
    return timeStamp;
  }

}
