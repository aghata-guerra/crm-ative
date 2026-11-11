package com.base.CRM.entity.dto;

import com.base.CRM.entity.Lead;
import com.base.CRM.enumClass.Origin;
import java.time.LocalDateTime;
import java.util.List;

public record LeadDto(
    Long id,
    String name,
    String email,
    String telephone,
    String position,
    Origin origin,
    LocalDateTime createdAt,
    Long empresaId,
    List<NegociacaoDto> negociacaoDtoList
) {

  public static LeadDto fromEntity(Lead lead){
    return new LeadDto(
        lead.getId(),
        lead.getName(),
        lead.getEmail(),
        lead.getTelephone(),
        lead.getPosition(),
        lead.getOrigin(),
        lead.getCreatedAt(),
        lead.getCompany().getId(),
        lead.getNegociacaoList() != null
            ? lead.getNegociacaoList().stream().filter(com.base.CRM.config.CrmAccess::canView).map(NegociacaoDto :: fromEntity).toList()
            : null
    );
  }

}
