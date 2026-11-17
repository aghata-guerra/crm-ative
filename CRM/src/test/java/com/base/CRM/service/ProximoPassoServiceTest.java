package com.base.CRM.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.base.CRM.entity.Negociacao;
import com.base.CRM.entity.ProximoPasso;
import com.base.CRM.entity.User;
import com.base.CRM.entity.dto.ProximoPassoCreateDto;
import com.base.CRM.entity.dto.ProximoPassoDto;
import com.base.CRM.enumClass.StatusProximoPasso;
import com.base.CRM.exception.NextContactDateCantBeAfterTodayException;
import com.base.CRM.exception.ProximoPassoAlreadyConcludeException;
import com.base.CRM.exception.ProximoPassoNotFoundException;
import com.base.CRM.exception.StatusProximoPassoCantReturnException;
import com.base.CRM.repository.NegociacaoRepository;
import com.base.CRM.repository.ProximoPassoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProximoPassoServiceTest {

  @InjectMocks
  private ProximoPassoService proximoPassoService;

  @Mock
  private ProximoPassoRepository proximoPassoRepository;

  @Mock
  private NegociacaoService negociacaoService;

  @Test
  @DisplayName("Deve criar um proximo passo")
  void shouldCreateProximoPasso() {
    Negociacao negociacao = new Negociacao();
    User user = new User(1L, "Usuário", "user@test.com", "senha123", null, true);
    ProximoPassoCreateDto dto = new ProximoPassoCreateDto(1L, 1L, "Ligar para cliente", LocalDate.now().plusDays(2), StatusProximoPasso.AGENDADA);

    when(negociacaoService.findNegociacaoById(1L)).thenReturn(negociacao);
    when(proximoPassoRepository.save(any(ProximoPasso.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ProximoPassoDto result = proximoPassoService.createProximoPasso(dto, user);

    assertNotNull(result);
  }

  @Test
  @DisplayName("Deve alterar status do proximo passo")
  void shouldChangeStatusProximoPasso() {
    ProximoPasso proximoPasso = new ProximoPasso();
    proximoPasso.setId(1L);
    proximoPasso.setStatusProximoPasso(StatusProximoPasso.AGENDADA);
    Negociacao negociacao = new Negociacao();
    negociacao.setId(1L);
    User user = new User();
    user.setId(1L);
    proximoPasso.setNegociacao(negociacao);
    proximoPasso.setUser(user);

    when(proximoPassoRepository.findById(1L)).thenReturn(Optional.of(proximoPasso));
    when(proximoPassoRepository.save(any(ProximoPasso.class))).thenReturn(proximoPasso);

    ProximoPassoDto result = proximoPassoService.changeStatusProximoPasso(1L, StatusProximoPasso.REALIZADA);

    assertNotNull(result);
    assertEquals(StatusProximoPasso.REALIZADA, result.statusProximoPasso());
  }

  @Test
  @DisplayName("Deve lancar excecao quando proximo passo ja realizado")
  void shouldThrowWhenProximoPassoAlreadyRealizada() {
    ProximoPasso proximoPasso = new ProximoPasso();
    proximoPasso.setId(1L);
    proximoPasso.setStatusProximoPasso(StatusProximoPasso.REALIZADA);

    when(proximoPassoRepository.findById(1L)).thenReturn(Optional.of(proximoPasso));

    assertThrows(StatusProximoPassoCantReturnException.class,
        () -> proximoPassoService.changeStatusProximoPasso(1L, StatusProximoPasso.VENCIDA));
  }

  @Test
  @DisplayName("Deve retornar proximo passo por id")
  void shouldFindProximoPassoById() {
    ProximoPasso proximoPasso = new ProximoPasso();
    proximoPasso.setId(1L);

    when(proximoPassoRepository.findById(1L)).thenReturn(Optional.of(proximoPasso));

    ProximoPasso result = proximoPassoService.findProximoPassoById(1L);

    assertNotNull(result);
    assertEquals(1L, result.getId());
  }

  @Test
  @DisplayName("Deve lancar excecao quando proximo passo nao encontrado")
  void shouldThrowWhenProximoPassoNotFound() {
    when(proximoPassoRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ProximoPassoNotFoundException.class, () -> proximoPassoService.findProximoPassoById(1L));
  }

  @Test
  @DisplayName("Deve listar proximos passos por negociacao")
  void shouldFindProximoPassoByNegociacaoId() {
    Negociacao negociacao = new Negociacao();
    negociacao.setId(1L);
    ProximoPasso proximoPasso = new ProximoPasso();
    proximoPasso.setId(1L);
    proximoPasso.setNegociacao(negociacao);
    proximoPasso.setUser(new User());

    when(negociacaoService.findNegociacaoById(1L)).thenReturn(negociacao);
    when(proximoPassoRepository.findByNegociacaoId(1L)).thenReturn(List.of(proximoPasso));

    List<ProximoPassoDto> result = proximoPassoService.findProximoPassoByNegociacaoId(1L);

    assertEquals(1, result.size());
  }

  @Test
  @DisplayName("Deve alterar data do proximo contato")
  void shouldAlterDateNextContact() {
    ProximoPasso proximoPasso = new ProximoPasso();
    proximoPasso.setId(1L);
    proximoPasso.setStatusProximoPasso(StatusProximoPasso.AGENDADA);
    proximoPasso.setExpectedDate(LocalDate.now().plusDays(1));
    Negociacao negociacao = new Negociacao();
    negociacao.setId(1L);
    User user = new User();
    user.setId(1L);
    proximoPasso.setNegociacao(negociacao);
    proximoPasso.setUser(user);

    when(proximoPassoRepository.findById(1L)).thenReturn(Optional.of(proximoPasso));
    when(proximoPassoRepository.save(any(ProximoPasso.class))).thenReturn(proximoPasso);

    ProximoPassoDto result = proximoPassoService.alterDateNextContact(LocalDate.now().plusDays(5), 1L);

    assertNotNull(result);
    assertEquals(LocalDate.now().plusDays(5), result.expectedDate());
  }

  @Test
  @DisplayName("Deve lancar excecao quando proximo passo ja concluido")
  void shouldThrowWhenProximoPassoAlreadyConclude() {
    ProximoPasso proximoPasso = new ProximoPasso();
    proximoPasso.setId(1L);
    proximoPasso.setStatusProximoPasso(StatusProximoPasso.REALIZADA);
    Negociacao negociacao = new Negociacao();
    negociacao.setId(1L);
    User user = new User();
    user.setId(1L);
    proximoPasso.setNegociacao(negociacao);
    proximoPasso.setUser(user);

    when(proximoPassoRepository.findById(1L)).thenReturn(Optional.of(proximoPasso));

    assertThrows(StatusProximoPassoCantReturnException.class,
        () -> proximoPassoService.alterDateNextContact(LocalDate.now().plusDays(5), 1L));
  }

  @Test
  @DisplayName("Deve lancar excecao quando data do proximo contato e passada")
  void shouldThrowWhenNextContactDatePast() {
    ProximoPasso proximoPasso = new ProximoPasso();
    proximoPasso.setId(1L);
    proximoPasso.setStatusProximoPasso(StatusProximoPasso.AGENDADA);

    when(proximoPassoRepository.findById(1L)).thenReturn(Optional.of(proximoPasso));

    assertThrows(NextContactDateCantBeAfterTodayException.class,
        () -> proximoPassoService.alterDateNextContact(LocalDate.now().minusDays(1), 1L));
  }

  @Test
  @DisplayName("Deve listar proximos passos por usuario")
  void shouldFindByProximoPassoByUserId() {
    ProximoPasso proximoPasso = new ProximoPasso();
    proximoPasso.setId(1L);
    Negociacao negociacao = new Negociacao();
    negociacao.setId(1L);
    User user = new User();
    user.setId(1L);
    proximoPasso.setNegociacao(negociacao);
    proximoPasso.setUser(user);
    when(proximoPassoRepository.findByUserId(1L)).thenReturn(List.of(proximoPasso));

    List<ProximoPassoDto> result = proximoPassoService.findByProximoPassoByUserId(1L);

    assertEquals(1, result.size());
  }
}
