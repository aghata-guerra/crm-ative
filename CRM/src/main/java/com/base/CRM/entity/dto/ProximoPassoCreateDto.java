package com.base.CRM.entity.dto;

import com.base.CRM.entity.Negociacao;
import com.base.CRM.entity.ProximoPasso;
import com.base.CRM.entity.User;
import com.base.CRM.enumClass.StatusProximoPasso;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ProximoPassoCreateDto(
    @NotNull(message = "Id da negociação precisa estar presente")
    Long negociacaoId,
    @NotNull(message = "Id do usuário precisa estar presente")
    Long userId,
    @NotBlank(message = "Descrição não pode ser ausente")
    String description,
    @NotNull(message = "Informe a data do contato")
    @FutureOrPresent(message = "Data só pode ser hoje ou no futuro")
    LocalDate expectedDate,
    @NotNull(message = "Status do próximo passo não pode estar em branco")
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
