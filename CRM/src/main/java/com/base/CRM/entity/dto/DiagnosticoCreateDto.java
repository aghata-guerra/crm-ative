package com.base.CRM.entity.dto;

import jakarta.validation.constraints.*;

import com.base.CRM.entity.Diagnostico;
import com.base.CRM.entity.Negociacao;
import com.base.CRM.entity.User;

public record DiagnosticoCreateDto(
    @NotNull Long negociacaoId,
    Long userId,
    @NotNull @PositiveOrZero Integer companySize,
    @NotBlank @Size(max=255) String currentSituation,
    @NotBlank @Size(max=255) String mainDifficulty,
    @NotBlank @Size(max=255) String needs
) {

  public Diagnostico toEntity(Negociacao negociacao, User user){
    return new Diagnostico(
      null,
      negociacao,
      user,
      companySize,
      currentSituation,
      mainDifficulty,
      needs
    );
  }
}
