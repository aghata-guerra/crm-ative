package com.base.CRM.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.base.CRM.entity.Lead;
import com.base.CRM.entity.Negociacao;
import com.base.CRM.entity.User;
import com.base.CRM.entity.dto.NegociacaoCreateDto;
import com.base.CRM.entity.dto.NegociacaoDto;
import com.base.CRM.enumClass.Etapa;
import com.base.CRM.enumClass.Origin;
import com.base.CRM.enumClass.StatusNegociacao;
import com.base.CRM.exception.NegociacaoClosedException;
import com.base.CRM.exception.NegociacaoNotFoundException;
import com.base.CRM.exception.StageChangeException;
import com.base.CRM.repository.NegociacaoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class NegociacaoServiceTest {

  @InjectMocks
  private NegociacaoService negociacaoService;

  @Mock
  private NegociacaoRepository negociacaoRepository;

  @Mock
  private LeadService leadService;

  @Mock
  private UserService userService;

  @Mock
  private BalanceService balanceService;

  @Test
  @DisplayName("Deve criar uma negociacao")
  void shouldCreateNegociacao() {
    Lead lead = new Lead();
    lead.setId(1L);
    User user = new User(1L, "Usuário", "user@test.com", "senha123", null, true);
    NegociacaoCreateDto dto = new NegociacaoCreateDto(1L, 1L, Origin.GOOGLE, Etapa.NOVO_LEAD, BigDecimal.valueOf(1000));

    when(leadService.findLeadById(1L)).thenReturn(lead);
    when(userService.findUserById(1L)).thenReturn(user);
    when(negociacaoRepository.save(any(Negociacao.class))).thenAnswer(invocation -> invocation.getArgument(0));

    NegociacaoDto result = negociacaoService.createNegociacao(dto);

    assertNotNull(result);
  }

  @Test
  @DisplayName("Deve retornar negociacao por id")
  void shouldFindNegociacaoById() {
    Negociacao negociacao = new Negociacao();
    negociacao.setId(1L);

    when(negociacaoRepository.findById(1L)).thenReturn(Optional.of(negociacao));

    Negociacao result = negociacaoService.findNegociacaoById(1L);

    assertNotNull(result);
    assertEquals(1L, result.getId());
  }

  @Test
  @DisplayName("Deve lancar excecao quando negociacao nao encontrada")
  void shouldThrowWhenNegociacaoNotFound() {
    when(negociacaoRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NegociacaoNotFoundException.class, () -> negociacaoService.findNegociacaoById(1L));
  }

  @Test
  @DisplayName("Deve listar negociacoes por lead")
  void shouldFindAllNegociacaoByLeadId() {
    Negociacao negociacao = new Negociacao();
    Lead lead = new Lead();
    lead.setId(1L);
    User user = new User();
    user.setId(1L);
    negociacao.setLead(lead);
    negociacao.setUser(user);
    when(negociacaoRepository.findByLead_Id(1L)).thenReturn(List.of(negociacao));

    List<NegociacaoDto> result = negociacaoService.findAllNegociacaoByLeadId(1L);

    assertEquals(1, result.size());
  }

  @Test
  @DisplayName("Deve listar negociacoes por usuario")
  void shouldFindAllNegociacaoByUserId() {
    Negociacao negociacao = new Negociacao();
    Lead lead = new Lead();
    lead.setId(1L);
    User user = new User();
    user.setId(1L);
    negociacao.setLead(lead);
    negociacao.setUser(user);
    when(negociacaoRepository.findByUser_Id(1L)).thenReturn(List.of(negociacao));

    List<NegociacaoDto> result = negociacaoService.findAllNegociacaoByUserId(1L);

    assertEquals(1, result.size());
  }

  @Test
  @DisplayName("Deve listar todas negociacoes")
  void shouldFindAllNegociacao() {
    Negociacao negociacao = new Negociacao();
    Lead lead = new Lead();
    lead.setId(1L);
    User user = new User();
    user.setId(1L);
    negociacao.setLead(lead);
    negociacao.setUser(user);
    when(negociacaoRepository.findAll()).thenReturn(List.of(negociacao));

    List<NegociacaoDto> result = negociacaoService.findAllNegociacao();

    assertEquals(1, result.size());
  }

  @Test
  @DisplayName("Deve listar negociacoes paginadas")
  void shouldFindAllNegociacaoPaginated() {
    Negociacao negociacao = new Negociacao();
    Lead lead = new Lead();
    lead.setId(1L);
    User user = new User();
    user.setId(1L);
    negociacao.setLead(lead);
    negociacao.setUser(user);
    Page<Negociacao> page = new PageImpl<>(List.of(negociacao));

    when(negociacaoRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

    Page<NegociacaoDto> result = negociacaoService.findAllNegociacao(org.springframework.data.domain.PageRequest.of(0, 10));

    assertEquals(1, result.getTotalElements());
  }

  @Test
  @DisplayName("Deve atualizar status da negociacao")
  void shouldUpdateStatusNegociacao() {
    Negociacao negociacao = new Negociacao();
    negociacao.setId(1L);
    negociacao.setStatusNegociacao(StatusNegociacao.EM_NEGOCIACAO);
    Lead lead = new Lead();
    lead.setId(1L);
    User user = new User();
    user.setId(1L);
    negociacao.setLead(lead);
    negociacao.setUser(user);

    when(negociacaoRepository.findById(1L)).thenReturn(Optional.of(negociacao));
    when(negociacaoRepository.save(any(Negociacao.class))).thenReturn(negociacao);

    NegociacaoDto result = negociacaoService.updateStatusNegociacao(1L, StatusNegociacao.ENCERRADA_GANHA);

    assertNotNull(result);
  }

  @Test
  @DisplayName("Deve atualizar stage da negociacao valida")
  void shouldUpdateStageNegociacaoValida() {
    Negociacao negociacao = new Negociacao();
    negociacao.setId(1L);
    negociacao.setStatusNegociacao(StatusNegociacao.EM_NEGOCIACAO);
    negociacao.setStage(Etapa.NOVO_LEAD);
    Lead lead = new Lead();
    lead.setId(1L);
    User user = new User();
    user.setId(1L);
    negociacao.setLead(lead);
    negociacao.setUser(user);

    when(negociacaoRepository.findById(1L)).thenReturn(Optional.of(negociacao));
    when(negociacaoRepository.save(any(Negociacao.class))).thenReturn(negociacao);

    NegociacaoDto result = negociacaoService.updateStageNegociacao(1L, Etapa.QUALIFICACAO);

    assertNotNull(result);
    assertEquals(Etapa.QUALIFICACAO, result.stage());
  }

  @Test
  @DisplayName("Deve lancar excecao ao atualizar stage de negociacao fechada")
  void shouldThrowWhenUpdateStageFechada() {
    Negociacao negociacao = new Negociacao();
    negociacao.setId(1L);
    negociacao.setStatusNegociacao(StatusNegociacao.ENCERRADA_GANHA);

    when(negociacaoRepository.findById(1L)).thenReturn(Optional.of(negociacao));

    assertThrows(NegociacaoClosedException.class,
        () -> negociacaoService.updateStageNegociacao(1L, Etapa.QUALIFICACAO));
  }

  @Test
  @DisplayName("Deve avancar para proximo stage")
  void shouldNextStageUpdate() {
    Negociacao negociacao = new Negociacao();
    negociacao.setId(1L);
    negociacao.setStatusNegociacao(StatusNegociacao.EM_NEGOCIACAO);
    negociacao.setStage(Etapa.NOVO_LEAD);
    Lead lead = new Lead();
    lead.setId(1L);
    User user = new User();
    user.setId(1L);
    negociacao.setLead(lead);
    negociacao.setUser(user);

    when(negociacaoRepository.findById(1L)).thenReturn(Optional.of(negociacao));
    when(negociacaoRepository.save(any(Negociacao.class))).thenReturn(negociacao);

    NegociacaoDto result = negociacaoService.nextStageUpdate(1L);

    assertNotNull(result);
    assertEquals(Etapa.QUALIFICACAO, result.stage());
  }

  @Test
  @DisplayName("Deve lancar excecao ao avancar stage invalido")
  void shouldThrowWhenNextStageInvalido() {
    Negociacao negociacao = new Negociacao();
    negociacao.setId(1L);
    negociacao.setStatusNegociacao(StatusNegociacao.EM_NEGOCIACAO);
    negociacao.setStage(Etapa.DIAGNOSTICO); // valor fora do switch esperado

    when(negociacaoRepository.findById(1L)).thenReturn(Optional.of(negociacao));

    assertThrows(StageChangeException.class,
        () -> negociacaoService.nextStageUpdate(1L));
  }

  @Test
  @DisplayName("Deve finalizar negociacao como ganho")
  void shouldFinishNegociacaoAsGain() {
    Negociacao negociacao = new Negociacao();
    negociacao.setId(1L);
    negociacao.setStatusNegociacao(StatusNegociacao.EM_NEGOCIACAO);
    negociacao.setEstimatedCost(BigDecimal.valueOf(1000));
    Lead lead = new Lead();
    lead.setId(1L);
    User user = new User();
    user.setId(1L);
    negociacao.setLead(lead);
    negociacao.setUser(user);

    when(negociacaoRepository.findById(1L)).thenReturn(Optional.of(negociacao));
    when(negociacaoRepository.save(any(Negociacao.class))).thenReturn(negociacao);

    NegociacaoDto result = negociacaoService.finishNegociacaoAsGain(1L);

    assertNotNull(result);
    verify(balanceService).addBalanceEarnings(BigDecimal.valueOf(1000));
  }

  @Test
  @DisplayName("Deve finalizar negociacao como perda")
  void shouldFinishNegociacaoAsLoss() {
    Negociacao negociacao = new Negociacao();
    negociacao.setId(1L);
    negociacao.setStatusNegociacao(StatusNegociacao.EM_NEGOCIACAO);
    negociacao.setEstimatedCost(BigDecimal.valueOf(1000));
    Lead lead = new Lead();
    lead.setId(1L);
    User user = new User();
    user.setId(1L);
    negociacao.setLead(lead);
    negociacao.setUser(user);

    when(negociacaoRepository.findById(1L)).thenReturn(Optional.of(negociacao));
    when(negociacaoRepository.save(any(Negociacao.class))).thenReturn(negociacao);

    NegociacaoDto result = negociacaoService.finishNegociacaoAsLoss(1L, "Motivo da perda");

    assertNotNull(result);
    verify(balanceService).subtractBalanceEarnings(BigDecimal.valueOf(-1000));
  }
}
