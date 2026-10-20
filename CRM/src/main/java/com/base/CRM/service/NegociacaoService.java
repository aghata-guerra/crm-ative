package com.base.CRM.service;

import com.base.CRM.entity.Lead;
import com.base.CRM.entity.Negociacao;
import com.base.CRM.entity.User;
import com.base.CRM.entity.dto.NegociacaoCreateDto;
import com.base.CRM.entity.dto.NegociacaoDto;
import com.base.CRM.enumClass.Etapa;
import com.base.CRM.enumClass.StatusNegociacao;
import com.base.CRM.exception.NegociacaoNotFoundException;
import com.base.CRM.repository.NegociacaoRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class NegociacaoService {

  private final NegociacaoRepository negociacaoRepository;
  private final LeadService leadService;
  private final UserService userService;

  public NegociacaoService(NegociacaoRepository negociacaoRepository,
      LeadService leadService) {
    this.negociacaoRepository = negociacaoRepository;
    this.leadService = leadService;
  }

  public NegociacaoDto createNegociacao(NegociacaoCreateDto negociacaoDto) {

    Lead lead = leadService.findLeadById(negociacaoDto.leadId());
    User user = userService.findUserById(negociacaoDto.userId());

    return NegociacaoDto.fromEntity(
        negociacaoRepository.save(negociacaoDto.toEntity(lead, user))
    );
  }

  public Negociacao findNegociacaoById(Long id) {
    return negociacaoRepository.findById(id).orElseThrow(NegociacaoNotFoundException::new);
  }

  public List<NegociacaoDto> findAllNegociacaoByLeadId(Long leadId){
    return negociacaoRepository.findByLead_Id(leadId).stream().map(
        NegociacaoDto::fromEntity
    ).toList();
  }

  public List<NegociacaoDto> findAllNegociacaoByUserId(Long userId) {
    return negociacaoRepository.findByUser_Id(userId).stream().map(
        NegociacaoDto::fromEntity
    ).toList();
  }

  public NegociacaoDto updateStatusNegociacao(Long negociacaoId, StatusNegociacao statusNegociacao) {

    Negociacao negociacao = findNegociacaoById(negociacaoId);

    negociacao.setStatusNegociacao(statusNegociacao);

    return NegociacaoDto.fromEntity(negociacaoRepository.save(negociacao));

  }

  public NegociacaoDto updateStageNegociacao(Long negociacaoId, Etapa etapa) {

    Negociacao negociacao = findNegociacaoById(negociacaoId);
    negociacao.setStage(etapa);
    negociacao.setUpdatedStageDate(LocalDate.now());
    return NegociacaoDto.fromEntity(negociacaoRepository.save(negociacao));

  }




}
