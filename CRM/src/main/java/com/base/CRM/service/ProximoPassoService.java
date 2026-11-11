package com.base.CRM.service;

import com.base.CRM.entity.Negociacao;
import com.base.CRM.entity.ProximoPasso;
import com.base.CRM.entity.User;
import com.base.CRM.entity.dto.ProximoPassoCreateDto;
import com.base.CRM.entity.dto.ProximoPassoDto;
import com.base.CRM.enumClass.StatusProximoPasso;
import com.base.CRM.exception.NegociacaoNotFoundException;
import com.base.CRM.exception.NextContactDateCantBeAfterTodayException;
import com.base.CRM.exception.ProximoPassoAlreadyConcludeException;
import com.base.CRM.exception.ProximoPassoNotFoundException;
import com.base.CRM.exception.StatusProximoPassoCantReturnException;
import com.base.CRM.repository.NegociacaoRepository;
import com.base.CRM.repository.ProximoPassoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

@Service
@org.springframework.transaction.annotation.Transactional
public class ProximoPassoService {

  private final ProximoPassoRepository proximoPassoRepository;
  private final NegociacaoService negociacaoService;

  public ProximoPassoService(ProximoPassoRepository proximoPassoRepository,
      NegociacaoService negociacaoService) {
    this.proximoPassoRepository = proximoPassoRepository;
    this.negociacaoService = negociacaoService;
  }

  public ProximoPassoDto createProximoPasso(ProximoPassoCreateDto proximoPassoCreateDto, User user){

    Negociacao negociacaoHolder = negociacaoService.findNegociacaoById(proximoPassoCreateDto.negociacaoId());

    negociacaoService.requireOpen(negociacaoHolder);
    return ProximoPassoDto.fromEntity(proximoPassoRepository.save(
      proximoPassoCreateDto.toEntity(negociacaoHolder, user)
    ));
  }

  public ProximoPassoDto changeStatusProximoPasso(Long proximoPassoId, StatusProximoPasso statusProximoPasso){

    ProximoPasso proximoPasso = findProximoPassoById(proximoPassoId);
    negociacaoService.requireOpen(proximoPasso.getNegociacao());

    if (statusProximoPasso == null) throw new IllegalArgumentException("Informe um status válido.");
    if(proximoPasso.getStatusProximoPasso() == StatusProximoPasso.REALIZADA){
      throw new StatusProximoPassoCantReturnException();
    }

    if(statusProximoPasso == StatusProximoPasso.REALIZADA){
      proximoPasso.setFinishedAt(LocalDate.now());
    }

    proximoPasso.setStatusProximoPasso(statusProximoPasso);

    return ProximoPassoDto.fromEntity(
        proximoPassoRepository.save(proximoPasso)
    );

  }

  public ProximoPasso findProximoPassoById(Long proximoPassoId){

    ProximoPasso step = proximoPassoRepository.findById(proximoPassoId)
        .orElseThrow(ProximoPassoNotFoundException::new);
    negociacaoService.findNegociacaoById(step.getNegociacao().getId());
    return step;

  }

  public List<ProximoPassoDto> findProximoPassoByNegociacaoId(Long negociacaoId){

    negociacaoService.findNegociacaoById(negociacaoId);

    return proximoPassoRepository.findByNegociacaoId(negociacaoId).stream()
        .map(
            ProximoPassoDto::fromEntity
        ).toList();

  }

  public List<ProximoPassoDto> findByExpectedDateBetweenAndStatus(LocalDate startDate, LocalDate endDate, StatusProximoPasso statusProximoPasso){
    com.base.CRM.config.CrmAccess.requireManager();
    if (startDate == null || endDate == null || endDate.isBefore(startDate) || statusProximoPasso == null) {
      throw new IllegalArgumentException("Confira o período e o status informados.");
    }
    return proximoPassoRepository.findByExpectedDateBetween(startDate, endDate)
        .stream().map(ProximoPassoDto::fromEntity)
        .filter(step -> step.statusProximoPasso() == statusProximoPasso).toList();
  }

  public List<ProximoPassoDto> findByProximoPassoByUserId(Long userId){
    com.base.CRM.config.CrmAccess.requireUser(userId);
    return proximoPassoRepository.findByUserId(userId)
        .stream()
        .map(ProximoPassoDto::fromEntity)
        .toList();
  }

  public ProximoPassoDto alterDateNextContact(LocalDate nextContact, Long proximoPassoId){

    ProximoPasso proximoPasso = findProximoPassoById(proximoPassoId);
    negociacaoService.requireOpen(proximoPasso.getNegociacao());

    if(verifyIfProximoPassoIsValid(proximoPasso)){

      if(proximoPasso.getStatusProximoPasso() == StatusProximoPasso.REALIZADA){
        throw new ProximoPassoAlreadyConcludeException();
      }

      if(nextContact == null || nextContact.isBefore(LocalDate.now())){
        throw new NextContactDateCantBeAfterTodayException();
      }

      proximoPasso.setExpectedDate(nextContact);

      if (proximoPasso.getStatusProximoPasso() == StatusProximoPasso.VENCIDA){
        proximoPasso.setStatusProximoPasso(StatusProximoPasso.AGENDADA);
      }

      return ProximoPassoDto.fromEntity(proximoPassoRepository.save(proximoPasso));

    } else {
      throw new StatusProximoPassoCantReturnException();
    }

  }

  public Boolean verifyIfProximoPassoIsValid(ProximoPasso proximoPasso){
    if (proximoPasso.getStatusProximoPasso() == StatusProximoPasso.REALIZADA){
      return false;
    } else {
      return true;
    }
  }


}
