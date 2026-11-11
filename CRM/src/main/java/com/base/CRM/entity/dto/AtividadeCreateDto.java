package com.base.CRM.entity.dto;

import jakarta.validation.constraints.*;

import com.base.CRM.entity.Atividade;
import com.base.CRM.entity.Negociacao;
import com.base.CRM.entity.User;
import com.base.CRM.enumClass.Tipo;
import java.time.LocalDate;

public record AtividadeCreateDto(
    @NotNull Long negociacaoId,
    Long userId,
    @NotNull Tipo type,
    @NotBlank @Size(max=255) String description,
    @NotNull @PastOrPresent LocalDate happenDate
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
