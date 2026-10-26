package com.base.CRM.exception;

public class UserAlreadyRegistered extends RuntimeException {

  public UserAlreadyRegistered() {
    super("Usuário já registrado!");
  }
}
