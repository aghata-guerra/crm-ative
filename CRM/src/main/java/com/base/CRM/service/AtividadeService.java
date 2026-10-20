package com.base.CRM.service;

import com.base.CRM.entity.Negociacao;
import com.base.CRM.entity.User;
import com.base.CRM.entity.dto.AtividadeCreateDto;
import com.base.CRM.entity.dto.AtividadeDto;
import com.base.CRM.exception.NegociacaoNotFoundException;
import com.base.CRM.repository.AtividadeRepository;
import com.base.CRM.repository.NegociacaoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AtividadeService {

  private final AtividadeRepository atividadeRepository;
  private final NegociacaoService negociacaoService;
  private final NegociacaoRepository negociacaoRepository;

  public AtividadeService(AtividadeRepository atividadeRepository,
      NegociacaoService negociacaoService, NegociacaoRepository negociacaoRepository) {
    this.atividadeRepository = atividadeRepository;
    this.negociacaoService = negociacaoService;
    this.negociacaoRepository = negociacaoRepository;
  }

  public AtividadeDto createAtividade(AtividadeCreateDto atividadeCreateDto, User user){

    Negociacao negociacao = negociacaoService.findNegociacaoById(atividadeCreateDto.negociacaoId());

    return AtividadeDto.fromEntity(atividadeRepository.save(atividadeCreateDto.toEntity(negociacao, user)));

  }

  public List<AtividadeDto> findAllAtividadesByNegociacaoId(Long negociacaoId){

    Optional<Negociacao> negociacao = negociacaoRepository.findById(negociacaoId);

    if(negociacao.isPresent()){

    return atividadeRepository.findAllByNegociacaoId(negociacaoId)
        .stream()
        .map(
            AtividadeDto::fromEntity
        )
        .toList();
    } else {
      throw new NegociacaoNotFoundException();
    }

  }

}
