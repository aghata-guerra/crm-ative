package com.base.CRM.entity.dto;

import com.base.CRM.entity.Lead;
import java.time.LocalDateTime;
import java.util.List;

public record LeadDto(
    Long id,
    String name,
    String email,
    String telephone,
    String position,
    LocalDateTime createdAt,
    Long companyId,
    List<NegociacaoDto> negociacaoDtoList
) {

  public static LeadDto fromEntity(Lead lead){
    return new LeadDto(
        lead.getId(),
        lead.getName(),
        lead.getEmail(),
        lead.getTelephone(),
        lead.getPosition(),
        lead.getCreatedAt(),
        lead.getCompany().getId(),
        lead.getNegociacaoList() != null
            ? lead.getNegociacaoList().stream().map(NegociacaoDto :: fromEntity).toList()
            : null
    );
  }

}
