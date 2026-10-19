package com.base.CRM.service;

import com.base.CRM.consult.ConsultApiCnpj;
import com.base.CRM.entity.dto.EmpresaCreateDto;
import com.base.CRM.entity.dto.EmpresaDto;
import com.base.CRM.exception.EmpresaNotFoundException;
import com.base.CRM.exception.IncorrectCnpjException;
import com.base.CRM.repository.EmpresaRepository;
import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EmpresaService {

  private final EmpresaRepository empresaRepository;
  private final ConsultApiCnpj consultApiCnpj;

  public EmpresaService(EmpresaRepository empresaRepository, ConsultApiCnpj consultApiCnpj) {
    this.empresaRepository = empresaRepository;
    this.consultApiCnpj = consultApiCnpj;
  }

  // Create a new Empresa

  public EmpresaDto createEmpresa(String cnpj) throws IOException, InterruptedException {

    if (cnpj.length() != 18){
      throw new IncorrectCnpjException();
    } else {
      EmpresaCreateDto empresaCreateDto = consultApiCnpj.consultCnpj(cnpj);
      return EmpresaDto.fromEntity(empresaRepository.save(empresaCreateDto.toEntity()));
    }

  }

  // Read all Empresas

  public List<EmpresaDto> findAllEmpresas(){
    return empresaRepository.findAll().stream().map(EmpresaDto::fromEntity).toList();
  }

  // Read Empresa by Id

  public EmpresaDto findEmpresaById(Long id){
    return EmpresaDto.fromEntity(empresaRepository.findById(id).orElseThrow(EmpresaNotFoundException::new));
  }

  // Read Empresa By Cnpj

  public EmpresaDto findEmpresaByCnpj(String cnpj){

    if(cnpj.length() != 18){
      throw new IncorrectCnpjException();
    } else {
      return EmpresaDto.fromEntity(empresaRepository.findByCnpj(cnpj).orElseThrow(EmpresaNotFoundException::new));
    }

  }
}
