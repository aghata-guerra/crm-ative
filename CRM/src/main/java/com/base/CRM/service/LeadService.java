package com.base.CRM.service;


import com.base.CRM.entity.Empresa;
import com.base.CRM.entity.dto.LeadCreateDto;
import com.base.CRM.entity.dto.LeadDto;
import com.base.CRM.repository.LeadRepository;
import org.springframework.stereotype.Service;

@Service
public class LeadService {

  private final LeadRepository leadRepository;
  private final EmpresaService empresaService;

  public LeadService(LeadRepository leadRepository, EmpresaService empresaService) {
    this.leadRepository = leadRepository;
    this.empresaService = empresaService;
  }

  // Create lead

  public LeadDto createLead(LeadCreateDto lead){

    Empresa holder = empresaService.findEmpresaById(lead.empresaId());

    return LeadDto.fromEntity(
      leadRepository.save(lead.toEntity(holder))
    );

  }


}
