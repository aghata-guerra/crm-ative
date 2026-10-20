package com.base.CRM.exception;

public class ServerApiErrorException extends RuntimeException {

  public ServerApiErrorException(String message) {
    super(message);
  }
}
