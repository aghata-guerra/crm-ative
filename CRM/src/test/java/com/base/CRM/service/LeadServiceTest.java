package com.base.CRM.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import com.base.CRM.entity.Empresa;
import com.base.CRM.entity.Lead;
import com.base.CRM.entity.dto.LeadCreateDto;
import com.base.CRM.entity.dto.LeadDto;
import com.base.CRM.repository.LeadRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LeadServiceTest {

  @InjectMocks
  private LeadService leadService;

  @Mock
  private EmpresaService empresaService;

  @Mock
  private LeadRepository leadRepository;

  @Test
  @DisplayName("Deve retornar um Lead")
  public void shouldReturnLead(){

    Empresa empresa = new Empresa(1L , "EmpresaTeste", "EmpresaTeste", "95.282.948/0001-93", "8112345678", "empresa@empresa.com");
    LeadCreateDto leadHolder = new LeadCreateDto(
        "leadTeste", "123@gmail.com", "1234567",
        "CEO", 1L
    );
    Lead lead = new Lead(
        1L, "leadTeste", "123@gmail.com", "1234567",
        "CEO", empresa
    );
    Mockito.when(empresaService.findEmpresaById(any(Long.class))).thenReturn(empresa);
    Mockito.when(leadRepository.save(any(Lead.class))).thenReturn(lead);

    LeadDto expectLead = leadService.createLead(leadHolder);

    assertAll(
        () -> assertEquals(1L, expectLead.id(), "Id do lead incorreto"),
        () -> assertEquals("leadTeste", expectLead.name(), "Nome do lead incorreto"),
        () -> assertEquals("123@gmail.com", expectLead.email(), "Email do lead incorreto"),
        () -> assertEquals("1234567", expectLead.telephone(), "Telefone do lead incorreto"),
        () -> assertEquals("CEO", expectLead.position(), "Posição do Lead incorreta"),
        () -> assertEquals(1L, expectLead.companyId(), "Id da empres do lead incorreta")
    );


  }


}