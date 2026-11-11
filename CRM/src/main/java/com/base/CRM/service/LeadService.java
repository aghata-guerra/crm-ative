package com.base.CRM.service;


import com.base.CRM.entity.Empresa;
import com.base.CRM.entity.Lead;
import com.base.CRM.entity.dto.LeadCreateDto;
import com.base.CRM.entity.dto.LeadDto;
import com.base.CRM.exception.LeadAlreadyExistsException;
import com.base.CRM.exception.LeadNotFoundException;
import com.base.CRM.repository.LeadRepository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LeadService {

  private final LeadRepository leadRepository;
  private final EmpresaService empresaService;

  public LeadService(LeadRepository leadRepository, EmpresaService empresaService) {
    this.leadRepository = leadRepository;
    this.empresaService = empresaService;
  }

  // Create lead
  
  @Transactional
  public LeadDto createLead(LeadCreateDto lead){

    Optional<Lead> holderLead = consultIfLeadsRegistered(lead);

    if(holderLead.isPresent()){
      throw new LeadAlreadyExistsException("E-mail ou telefone já cadastrado no sistema!", holderLead.get().getId());
    }
    
    Empresa holder = empresaService.findEmpresaById(lead.empresaId());

    return LeadDto.fromEntity(
      leadRepository.save(lead.toEntity(holder))
    );

  }

  // Find All Leads

  public Page<LeadDto> findAllLeads(Pageable pageable){

    return leadRepository.findAll(pageable)
        .map(
            LeadDto::fromEntity
        );

  }

  // Find Lead by Id

  public LeadDto getLead(Long id) {
    return LeadDto.fromEntity(findLeadById(id));
  }

  public Lead findLeadById(Long id){
    return leadRepository.findById(id).orElseThrow(LeadNotFoundException::new);
  }
  
  // Find Lead by email
  
  public LeadDto findLeadByEmail(String email){
    
    email = email.trim().toLowerCase();
    
    return LeadDto.fromEntity(leadRepository.findByEmail(email).orElseThrow(LeadNotFoundException::new));
    
  }
  
  // Find Lead by Name
  
  public List<LeadDto> findLeadByName(String name){
    
    String normalizedName = name.trim().toLowerCase();
    
    return leadRepository.findByNameContainingIgnoreCase(name)
        .stream()
        .map(LeadDto::fromEntity)
        .toList();
    
  }
  

  // Find All Leads by Empresa Id

  public List<LeadDto> findAllLeadsByCompanyId(Long companyId){

    return leadRepository.findByCompanyId(companyId)
        .stream()
        .map(
            LeadDto::fromEntity
        ).toList();

  }
  
  private Optional<Lead> consultIfLeadsRegistered(LeadCreateDto leadCreateDto){

    Optional<Lead> holderLead = leadRepository.findByEmail(leadCreateDto.email().toLowerCase().trim());

    if(holderLead.isPresent()){
      return holderLead;
    }
        
    holderLead = leadRepository.findByTelephone(leadCreateDto.telephone().trim());

    return holderLead;

  }

}
