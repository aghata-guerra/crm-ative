package com.base.CRM.exception;

public class UserNotFoundException extends NotFoundException {

  public UserNotFoundException() {
    super("Usuário não localizado!");
  }
}
