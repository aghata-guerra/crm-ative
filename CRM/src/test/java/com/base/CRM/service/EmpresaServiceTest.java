package com.base.CRM.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import com.base.CRM.consult.ConsultApiCnpj;
import com.base.CRM.entity.Empresa;
import com.base.CRM.entity.dto.EmpresaCreateDto;
import com.base.CRM.entity.dto.EmpresaDto;
import com.base.CRM.entity.dto.TelefoneCreateDto;
import com.base.CRM.exception.IncorrectCnpjException;
import com.base.CRM.repository.EmpresaRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {

  @InjectMocks
  private EmpresaService empresaService;
  @Mock
  private EmpresaRepository empresaRepository;
  @Mock
  private ConsultApiCnpj consultApiCnpj;

  @Test
  @DisplayName("Deve retornar uma empresa")
  public void shouldReturnEmpresa() throws IOException, InterruptedException {
    TelefoneCreateDto telefoneCreateDto = new TelefoneCreateDto("81", "12345678");
    EmpresaCreateDto empresaCreateDto = new EmpresaCreateDto("EmpresaTeste", "EmpresaTeste", "95.282.948/0001-93", new ArrayList<>(
        List.of(telefoneCreateDto)), "empresa@empresa.com");
    Empresa empresa = new Empresa(1L , "EmpresaTeste", "EmpresaTeste", "95.282.948/0001-93", "8112345678", "empresa@empresa.com");
    Mockito.when(consultApiCnpj.consultCnpj("95.282.948/0001-93")).thenReturn(empresaCreateDto);
    Mockito.when(empresaRepository.save(any(Empresa.class))).thenReturn(empresa);

    EmpresaDto expectedDto = empresaService.createEmpresa("95.282.948/0001-93");

    ;
    assertAll(
        () -> assertEquals(1L, expectedDto.id(), "Id incorreto"),
        () -> assertEquals("EmpresaTeste", expectedDto.companyName(), "CompanyName incorreto"),
        () -> assertEquals("EmpresaTeste", expectedDto.tradeName(), "TradeName incorreto"),
        () -> assertEquals("8112345678", expectedDto.telephone(), "Telefone incorreto")
    );

  }

  @Test
  @DisplayName("Deve lançar uma exceção de CNPJ incorreto")
  public void shouldThrowIncorrectCnpjException() throws IOException, InterruptedException {

    assertThrows(IncorrectCnpjException.class, () -> empresaService.createEmpresa("1234567"));

  }

  @Test
  @DisplayName("Deve retornar lista de empresas")
  public void shouldReturnListOfEmpresas(){
    Empresa empresa = new Empresa(1L , "EmpresaTeste", "EmpresaTeste", "95.282.948/0001-93", "8112345678", "empresa@empresa.com");

    Mockito.when(empresaRepository.findAll()).thenReturn(List.of(empresa));

    List<EmpresaDto> listEmpresas = empresaService.findAllEmpresas();

    assertAll(
        () -> assertEquals(1, listEmpresas.size()),
        () -> assertEquals(1L, listEmpresas.get(0).id())
    );


  }
}