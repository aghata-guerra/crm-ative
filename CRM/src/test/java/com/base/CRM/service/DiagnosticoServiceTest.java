package com.base.CRM.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.base.CRM.entity.Diagnostico;
import com.base.CRM.entity.Negociacao;
import com.base.CRM.entity.User;
import com.base.CRM.entity.dto.DiagnosticoCreateDto;
import com.base.CRM.entity.dto.DiagnosticoDto;
import com.base.CRM.exception.DiagnosticoNotFoundException;
import com.base.CRM.repository.DiagnosticoRepository;
import java.util.List;
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
class DiagnosticoServiceTest {

  @InjectMocks
  private DiagnosticoService diagnosticoService;

  @Mock
  private DiagnosticoRepository diagnosticoRepository;

  @Mock
  private UserService userService;

  @Mock
  private NegociacaoService negociacaoService;

  @Test
  @DisplayName("Deve criar um diagnostico")
  void shouldCreateDiagnostico() {
    Negociacao negociacao = new Negociacao();
    User user = new User(1L, "Usuário", "user@test.com", "senha123", null, true);
    DiagnosticoCreateDto dto = new DiagnosticoCreateDto(1L, 1L, 10, "Situacao", "Dificuldade", "Necessidade");

    when(negociacaoService.findNegociacaoById(1L)).thenReturn(negociacao);
    when(userService.findUserById(1L)).thenReturn(user);
    when(diagnosticoRepository.save(any(Diagnostico.class))).thenAnswer(invocation -> invocation.getArgument(0));

    DiagnosticoDto result = diagnosticoService.createDiagnostico(dto);

    assertNotNull(result);
  }

  @Test
  @DisplayName("Deve retornar diagnostico por id")
  void shouldFindDiagnosticoById() {
    Diagnostico diagnostico = new Diagnostico();
    diagnostico.setId(1L);

    when(diagnosticoRepository.findById(1L)).thenReturn(java.util.Optional.of(diagnostico));

    Diagnostico result = diagnosticoService.findDiagnosticoById(1L);

    assertNotNull(result);
    assertEquals(1L, result.getId());
  }

  @Test
  @DisplayName("Deve lancar excecao quando diagnostico nao encontrado")
  void shouldThrowWhenDiagnosticoNotFound() {
    when(diagnosticoRepository.findById(1L)).thenReturn(java.util.Optional.empty());

    assertThrows(DiagnosticoNotFoundException.class, () -> diagnosticoService.findDiagnosticoById(1L));
  }

  @Test
  @DisplayName("Deve atualizar um diagnostico")
  void shouldUpdateDiagnostico() {
    Diagnostico diagnostico = new Diagnostico();
    diagnostico.setId(1L);
    Negociacao negociacao = new Negociacao();
    negociacao.setId(1L);
    User user = new User();
    user.setId(1L);
    diagnostico.setNegociacao(negociacao);
    diagnostico.setUser(user);
    DiagnosticoCreateDto dto = new DiagnosticoCreateDto(1L, 1L, 20, "Nova situacao", "Nova dificuldade", "Nova necessidade");

    when(diagnosticoRepository.findById(1L)).thenReturn(java.util.Optional.of(diagnostico));
    when(diagnosticoRepository.save(any(Diagnostico.class))).thenReturn(diagnostico);

    DiagnosticoDto result = diagnosticoService.updateDiagnostico(1L, dto);

    assertNotNull(result);
    verify(diagnosticoRepository).save(diagnostico);
  }

  @Test
  @DisplayName("Deve listar todos os diagnosticos paginados")
  void shouldFindAllDiagnosticoPaginated() {
    Diagnostico diagnostico = new Diagnostico();
    diagnostico.setId(1L);
    Negociacao negociacao = new Negociacao();
    negociacao.setId(1L);
    User user = new User();
    user.setId(1L);
    diagnostico.setNegociacao(negociacao);
    diagnostico.setUser(user);
    Page<Diagnostico> page = new PageImpl<>(List.of(diagnostico));

    when(diagnosticoRepository.findAll(any(Pageable.class))).thenReturn(page);

    Page<DiagnosticoDto> result = diagnosticoService.findAllDiagnostico(org.springframework.data.domain.PageRequest.of(0, 10));

    assertEquals(1, result.getTotalElements());
  }
}
