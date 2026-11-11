package com.base.CRM.entity.dto;

import com.base.CRM.entity.ProximoPasso;
import com.base.CRM.enumClass.StatusProximoPasso;
import java.time.LocalDate;

public record ProximoPassoDto(
    Long id,
    Long negociacaoId,
    Long userId,
    String userName,
    String description,
    LocalDate expectedDate,
    StatusProximoPasso statusProximoPasso,
    LocalDate finishedAt,
    LocalDate createdAt
) {

  public static ProximoPassoDto fromEntity(ProximoPasso proximoPasso){
    return new ProximoPassoDto(
        proximoPasso.getId(),
        proximoPasso.getNegociacao().getId(),
        proximoPasso.getUser().getId(),
        proximoPasso.getUser().getName(),
        proximoPasso.getDescription(),
        proximoPasso.getExpectedDate(),
        proximoPasso.getStatusProximoPasso() != StatusProximoPasso.REALIZADA
            && proximoPasso.getExpectedDate() != null
            && proximoPasso.getExpectedDate().isBefore(LocalDate.now())
            ? StatusProximoPasso.VENCIDA : proximoPasso.getStatusProximoPasso(),
        proximoPasso.getFinishedAt(),
        proximoPasso.getCreatedAt()
    );
  }


}
