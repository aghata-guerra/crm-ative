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

    return ProximoPassoDto.fromEntity(proximoPassoRepository.save(
      proximoPassoCreateDto.toEntity(negociacaoHolder, user)
    ));
  }

  public ProximoPassoDto changeStatusProximoPasso(Long proximoPassoId, StatusProximoPasso statusProximoPasso){

    ProximoPasso proximoPasso = findProximoPassoById(proximoPassoId);

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

    return proximoPassoRepository.findById(proximoPassoId).orElseThrow(
        ProximoPassoNotFoundException::new
    );

  }

  public List<ProximoPassoDto> findProximoPassoByNegociacaoId(Long negociacaoId){

    negociacaoService.findNegociacaoById(negociacaoId);

    return proximoPassoRepository.findByNegociacaoId(negociacaoId).stream()
        .map(
            ProximoPassoDto::fromEntity
        ).toList();

  }

  public List<ProximoPassoDto> findByExpectedDateBetweenAndStatus(LocalDate startDate, LocalDate endDate, StatusProximoPasso statusProximoPasso){
    return proximoPassoRepository.findByExpectedDateBetweenAndStatus(startDate, endDate, statusProximoPasso)
        .stream()
        .map(ProximoPassoDto::fromEntity)
        .toList();
  }

  public List<ProximoPassoDto> findByProximoPassoByUserId(Long userId){
    return proximoPassoRepository.findByUserId(userId)
        .stream()
        .map(ProximoPassoDto::fromEntity)
        .toList();
  }

  public ProximoPassoDto alterDateNextContact(LocalDate nextContact, Long proximoPassoId){

    ProximoPasso proximoPasso = findProximoPassoById(proximoPassoId);

    if(verifyIfProximoPassoIsValid(proximoPasso)){

      if(proximoPasso.getStatusProximoPasso() == StatusProximoPasso.REALIZADA){
        throw new ProximoPassoAlreadyConcludeException();
      }

      if(nextContact.isBefore(LocalDate.now())){
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
