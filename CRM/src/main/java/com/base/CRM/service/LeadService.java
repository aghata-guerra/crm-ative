package com.base.CRM.service;


import com.base.CRM.entity.Empresa;
import com.base.CRM.entity.Lead;
import com.base.CRM.entity.dto.LeadCreateDto;
import com.base.CRM.entity.dto.LeadDto;
import com.base.CRM.exception.LeadNotFoundException;
import com.base.CRM.repository.LeadRepository;
import java.util.List;
import org.springframework.data.domain.Pageable;
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

  // Find All Leads

  public List<LeadDto> findAllLeads(Pageable pageable){

    return leadRepository.findAll(pageable)
        .stream()
        .map(
            LeadDto::fromEntity
        ).toList();

  }

  // Find Lead by Id

  public Lead findLeadById(Long id){
    return leadRepository.findById(id).orElseThrow(LeadNotFoundException::new);
  }

  // Find All Leads by Empresa Id

  public List<LeadDto> findAllLeadsByCompanyId(Long companyId){

    return leadRepository.findByCompanyId(companyId)
        .stream()
        .map(
            LeadDto::fromEntity
        ).toList();

  }

}
