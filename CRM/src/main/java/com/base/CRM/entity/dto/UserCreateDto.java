package com.base.CRM.entity.dto;

import com.base.CRM.entity.User;
import com.base.CRM.enumClass.Profile;

public record UserCreateDto(
    String name,
    String email,
    String password,
    Profile profile, // MUDAR ANTES DE SUBIR O PROGRAMA, SOMENTE PARA TESTES
    Boolean active
) {
  
  public User toEntity(){
    return new User(
        null,
        name,
        email,
        password,
        profile,
        active
    );
  }
  
}
