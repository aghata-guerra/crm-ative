package com.base.CRM.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.base.CRM.entity.Atividade;
import com.base.CRM.entity.Negociacao;
import com.base.CRM.entity.User;
import com.base.CRM.entity.dto.AtividadeCreateDto;
import com.base.CRM.entity.dto.AtividadeDto;
import com.base.CRM.enumClass.Tipo;
import com.base.CRM.exception.AtividadeNotFoundException;
import com.base.CRM.exception.NegociacaoNotFoundException;
import com.base.CRM.repository.AtividadeRepository;
import com.base.CRM.repository.NegociacaoRepository;
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
class AtividadeServiceTest {

  @InjectMocks
  private AtividadeService atividadeService;

  @Mock
  private AtividadeRepository atividadeRepository;

  @Mock
  private NegociacaoService negociacaoService;

  @Mock
  private NegociacaoRepository negociacaoRepository;

  @Test
  @DisplayName("Deve criar uma atividade")
  void shouldCreateAtividade() {
    User user = new User(1L, "Usuário Teste", "user@test.com", "senha123", null, true);
    Negociacao negociacao = new Negociacao();
    AtividadeCreateDto dto = new AtividadeCreateDto(1L, 1L, Tipo.REUNIAO, "Descricao da reuniao", LocalDate.now());

    when(negociacaoService.findNegociacaoById(1L)).thenReturn(negociacao);
    when(atividadeRepository.save(any(Atividade.class))).thenAnswer(invocation -> invocation.getArgument(0));

    AtividadeDto result = atividadeService.createAtividade(dto, user);

    assertNotNull(result);
    verify(negociacaoService).findNegociacaoById(1L);
    verify(atividadeRepository).save(any(Atividade.class));
  }

  @Test
  @DisplayName("Deve retornar atividade por id")
  void shouldFindAtividadeById() {
    Atividade atividade = new Atividade();
    atividade.setId(1L);
    Negociacao negociacao = new Negociacao();
    negociacao.setId(1L);
    User user = new User();
    user.setId(1L);
    atividade.setNegociacao(negociacao);
    atividade.setUser(user);

    when(atividadeRepository.findById(1L)).thenReturn(Optional.of(atividade));

    AtividadeDto result = atividadeService.findAtividadeById(1L);

    assertNotNull(result);
    assertEquals(1L, result.id());
  }

  @Test
  @DisplayName("Deve lancar excecao quando atividade nao encontrada")
  void shouldThrowWhenAtividadeNotFound() {
    when(atividadeRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(AtividadeNotFoundException.class, () -> atividadeService.findAtividadeById(1L));
  }

  @Test
  @DisplayName("Deve listar atividades por negociacao")
  void shouldFindAllAtividadesByNegociacaoId() {
    Negociacao negociacao = new Negociacao();
    negociacao.setId(1L);
    Atividade atividade = new Atividade();
    atividade.setId(1L);
    atividade.setNegociacao(negociacao);
    atividade.setUser(new User());

    when(negociacaoRepository.findById(1L)).thenReturn(Optional.of(negociacao));
    when(atividadeRepository.findAllByNegociacaoId(1L)).thenReturn(List.of(atividade));

    List<AtividadeDto> result = atividadeService.findAllAtividadesByNegociacaoId(1L);

    assertEquals(1, result.size());
    assertEquals(1L, result.get(0).id());
  }

  @Test
  @DisplayName("Deve lancar excecao quando negociacao nao encontrada")
  void shouldThrowWhenNegociacaoNotFound() {
    when(negociacaoRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NegociacaoNotFoundException.class,
        () -> atividadeService.findAllAtividadesByNegociacaoId(1L));
  }
}
