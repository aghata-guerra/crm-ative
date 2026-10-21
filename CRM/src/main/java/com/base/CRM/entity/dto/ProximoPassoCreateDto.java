package com.base.CRM.entity.dto;

import com.base.CRM.entity.Negociacao;
import com.base.CRM.entity.ProximoPasso;
import com.base.CRM.entity.User;
import com.base.CRM.enumClass.StatusProximoPasso;
import java.time.LocalDate;

public record ProximoPassoCreateDto(
    Long negociacaoId,
    Long userId,
    String description,
    LocalDate expectedDate,
    StatusProximoPasso statusProximoPasso
) {

  public ProximoPasso toEntity(Negociacao negociacao, User user){
    return new ProximoPasso(
        null,
        negociacao,
        user,
        description,
        expectedDate,
        statusProximoPasso
    );
  }

}
