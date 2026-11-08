package com.base.CRM.service;

import com.base.CRM.entity.Lead;
import com.base.CRM.entity.Negociacao;
import com.base.CRM.entity.User;
import com.base.CRM.entity.dto.NegociacaoCreateDto;
import com.base.CRM.entity.dto.NegociacaoDto;
import com.base.CRM.entity.dto.NegociacoesPorOrigemDto;
import com.base.CRM.enumClass.Etapa;
import com.base.CRM.enumClass.StatusNegociacao;
import com.base.CRM.exception.NegociacaoClosedException;
import com.base.CRM.exception.NegociacaoNotFoundException;
import com.base.CRM.repository.NegociacaoRepository;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class NegociacaoService {

  private final NegociacaoRepository negociacaoRepository;
  private final LeadService leadService;
  private final UserService userService;
  private final BalanceService balanceService;

  public NegociacaoService(NegociacaoRepository negociacaoRepository,
      LeadService leadService, UserService userService, BalanceService balanceService) {
    this.negociacaoRepository = negociacaoRepository;
    this.leadService = leadService;
    this.userService = userService;
    this.balanceService = balanceService;
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

  public List<NegociacaoDto> findAllNegociacao(){
    return negociacaoRepository.findAll().stream().map(
        NegociacaoDto::fromEntity
    ).toList();
  }

  public Page<NegociacaoDto> findAllNegociacao(Pageable pageable){
    return negociacaoRepository.findAll(pageable).map(
        NegociacaoDto::fromEntity
    );
  }

  // Só gestores podem acessar esse metódo

  public NegociacaoDto updateStatusNegociacao(Long negociacaoId, StatusNegociacao statusNegociacao) {

    Negociacao negociacao = findNegociacaoById(negociacaoId);

    negociacao.setStatusNegociacao(statusNegociacao);

    return NegociacaoDto.fromEntity(negociacaoRepository.save(negociacao));

  }

  public NegociacaoDto updateStageNegociacao(Long negociacaoId, Etapa etapa) {

    Negociacao negociacao = findNegociacaoById(negociacaoId);

    if(verifyIfNegociacaoIsValid(negociacao)){

    negociacao.setStage(etapa);
    negociacao.setUpdatedStageDate(LocalDate.now());
    return NegociacaoDto.fromEntity(negociacaoRepository.save(negociacao));

    } else {
      throw new NegociacaoClosedException();
    }

  }

  public Page<NegociacaoDto> findAllNegociacaoByEtapaAndStatusNegociacao(Etapa etapa, StatusNegociacao statusNegociacao, Pageable page) {
    return negociacaoRepository.findByFilters(etapa, statusNegociacao, page)
        .map(NegociacaoDto::fromEntity);
  }

  public Page<NegociacaoDto> findAllNegociacaoByEtapaAndStatusNegociacaoAndUserId(Etapa etapa, StatusNegociacao statusNegociacao, Long userId, Pageable page) {
    return negociacaoRepository.findByFiltersAndResponsavel(userId, etapa, statusNegociacao, page)
        .map(NegociacaoDto::fromEntity);
  }

  public Page<NegociacaoDto> findAllNegociacaoWithDeadLineAndByStatus
      (Integer daysOnHold, StatusNegociacao status, Pageable pageable){

    LocalDate deadLine = LocalDate.now().minusDays(daysOnHold);

    return negociacaoRepository.findByUpdatedStageDateBeforeAndStatusNegociacao(deadLine, status, pageable)
        .map(
            NegociacaoDto::fromEntity
        );

  }

  public Page<NegociacaoDto> findAllNegociacaoWithDeadLineAndByStatusAndUserId
      (Integer daysOnHold, StatusNegociacao status, Long userId, Pageable pageable){

    LocalDate deadLine = LocalDate.now().minusDays(daysOnHold);

    return negociacaoRepository.findByUpdatedStageDateBeforeAndStatusNegociacaoAndUser_Id(deadLine, status, userId, pageable)
        .map(
            NegociacaoDto::fromEntity
        );

  }

  public List<NegociacaoDto> findAllNegociacaoWithoutProximoPasso(){

    return negociacaoRepository.findByProximoPassoIsEmpty()
        .stream()
        .map(
            NegociacaoDto::fromEntity
        )
        .toList();
  }

  public List<NegociacaoDto> findAllNegociacaoWithoutProximoPassoAndUserId(
      Long userId
  ){

    return negociacaoRepository.findByProximoPassoIsEmpty()
        .stream()
        .map(
            NegociacaoDto::fromEntity
        )
        .toList();
  }

  public NegociacaoDto finishNegociacaoAsGain(Long negociacaoId){

    Negociacao negociacao = findNegociacaoById(negociacaoId);

    if(verifyIfNegociacaoIsValid(negociacao)){

    negociacao.setStatus(StatusNegociacao.ENCERRADA_GANHA);

    balanceService.addBalanceEarnings(negociacao.getEstimatedCost());

    return NegociacaoDto.fromEntity(
        negociacaoRepository.save(negociacao)
    );

    } else {
      throw new NegociacaoNotFoundException();
    }


  }

  public NegociacaoDto finishNegociacaoAsLoss(Long negociacaoId, String lossReason){

    Negociacao negociacao = findNegociacaoById(negociacaoId);

      if(verifyIfNegociacaoIsValid(negociacao)){

      negociacao.setStatus(StatusNegociacao.ENCERRADA_PERDA);

      balanceService.subtractBalanceEarnings(negociacao.getEstimatedCost().negate());

      negociacao.setLossReason(lossReason);

    return NegociacaoDto.fromEntity(
        negociacaoRepository.save(negociacao)
    );

    } else {
      throw new NegociacaoNotFoundException();
    }


  }

  public List<NegociacoesPorOrigemDto> countNegociacaoByOrigin() {
    return negociacaoRepository.countNegociacoesPorOrigem();
  }

  public Boolean verifyIfNegociacaoIsValid(Negociacao negociacao){

    if(negociacao.getStatusNegociacao() == StatusNegociacao.EM_NEGOCIACAO){
      return true;
    } else {
      return false;
    }
  }




}
