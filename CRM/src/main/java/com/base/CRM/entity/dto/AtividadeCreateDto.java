package com.base.CRM.entity.dto;

import com.base.CRM.entity.Atividade;
import com.base.CRM.entity.Negociacao;
import com.base.CRM.entity.User;
import com.base.CRM.enumClass.Tipo;
import java.time.LocalDate;

public record AtividadeCreateDto(
    Long negociacaoId,
    Long userId,
    Tipo type,
    String description,
    LocalDate happenDate
) {

  public Atividade toEntity(Negociacao negociacao, User user){
    return new Atividade(
        null,
        negociacao,
        user,
        type,
        description,
        happenDate
    );
  }

}
