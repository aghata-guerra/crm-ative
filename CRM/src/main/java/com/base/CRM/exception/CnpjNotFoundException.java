package com.base.CRM.exception;

public class CnpjNotFoundException extends NotFoundException {

  public CnpjNotFoundException() {
    super("Cnpj não localizado!");
  }
}
