package com.base.CRM.exception;

public class IncorrectCnpjException extends RuntimeException {

  public IncorrectCnpjException() {
    super("Cnpj incorreto ou inexistente");
  }
}
