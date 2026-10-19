package com.base.CRM.entity.dto;

public record TelefoneCreateDto(
    String ddd,
    String numero
) {

  public String getCompleteTelephone(){
    return ddd + numero;
  }

}
