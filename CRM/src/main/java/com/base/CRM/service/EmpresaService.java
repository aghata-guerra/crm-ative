package com.base.CRM.service;

import com.base.CRM.consult.ConsultApiCnpj;
import com.base.CRM.entity.dto.EmpresaCreateDto;
import com.base.CRM.entity.dto.EmpresaDto;
import com.base.CRM.exception.IncorrectCnpjException;
import com.base.CRM.repository.EmpresaRepository;
import java.io.IOException;
import org.springframework.stereotype.Service;

@Service
public class EmpresaService {

  private final EmpresaRepository empresaRepository;
  private final ConsultApiCnpj consultApiCnpj;

  public EmpresaService(EmpresaRepository empresaRepository, ConsultApiCnpj consultApiCnpj) {
    this.empresaRepository = empresaRepository;
    this.consultApiCnpj = consultApiCnpj;
  }

  public EmpresaDto createEmpresaDto(String cnpj) throws IOException, InterruptedException {

    if (cnpj.length() != 18){
      throw new IncorrectCnpjException();
    } else {
      EmpresaCreateDto empresaCreateDto = consultApiCnpj.consultCnpj(cnpj);
      return EmpresaDto.fromEntity(empresaRepository.save(empresaCreateDto.toEntity()));
    }

  }

}
