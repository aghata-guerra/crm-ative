package com.base.CRM.entity.dto;

import jakarta.validation.constraints.*;

import com.base.CRM.entity.Lead;
import com.base.CRM.entity.Negociacao;
import com.base.CRM.entity.User;
import com.base.CRM.enumClass.Etapa;
import com.base.CRM.enumClass.Origin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record NegociacaoCreateDto(
    @NotNull(message = "É necessário o Id do Lead")
    Long leadId,
    @NotNull(message = "É necessário o Id do usuário")
    Long userId,
    @NotNull(message = "A origem é obrigatória de ser preenchida")
    Origin origin,
    Etapa stage,
    @NotNull(message = "O valor estimado precisa ser preenchido")
    @PositiveOrZero(message = "O valor estimado deve ser positivo ou zero")
    @Digits(integer = 17, fraction = 2)
    BigDecimal estimatedCost
) {

  public Negociacao toEntity(Lead lead, User user){
    return new Negociacao(
        null,
        lead,
        user,
        origin,
        Etapa.NOVO_LEAD,
        estimatedCost
    );
  }

}
