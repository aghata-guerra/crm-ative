package com.base.CRM.service;

import com.base.CRM.consult.ConsultApiCnpj;
import com.base.CRM.entity.Empresa;
import com.base.CRM.entity.dto.EmpresaCreateDto;
import com.base.CRM.entity.dto.EmpresaDto;
import com.base.CRM.exception.EmpresaAlreadyExistsException;
import com.base.CRM.exception.EmpresaNotFoundException;
import com.base.CRM.exception.IncorrectCnpjException;
import com.base.CRM.repository.EmpresaRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
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

    if(cnpj == null){
      throw new IncorrectCnpjException();
    }

    String normalizedCnpj = cnpj
        .trim()
        .replace(".", "")
        .replace("-", "")
        .replace("/", "");

    if (normalizedCnpj.length() != 14){
      throw new IncorrectCnpjException();
    }

    Optional<Empresa> empresaOptional = empresaRepository.findByCnpj(normalizedCnpj);

    if(empresaOptional.isPresent()){
      throw new EmpresaAlreadyExistsException(empresaOptional.get().getId(), "Empresa já cadastrada!");
    }

    EmpresaCreateDto empresaCreateDto = consultApiCnpj.consultCnpj(normalizedCnpj);
    return EmpresaDto.fromEntity(empresaRepository.save(empresaCreateDto.toEntity()));

  }

  public EmpresaDto createEmpresaManual(EmpresaCreateDto empresaCreateDto){

    if(empresaCreateDto.cnpj() == null){
      throw new IncorrectCnpjException();
    }

    String normalizedCnpj = empresaCreateDto.cnpj()
        .trim()
        .replace(".", "")
        .replace("-", "")
        .replace("/", "");

    if (normalizedCnpj.length() != 14){
      throw new IncorrectCnpjException();
    }

    Optional<Empresa> empresaOptional = empresaRepository.findByCnpj(normalizedCnpj);

    if(empresaOptional.isPresent()){
      throw new EmpresaAlreadyExistsException(empresaOptional.get().getId(), "Empresa já cadastrada!");
    }

    return EmpresaDto.fromEntity(empresaRepository.save(empresaCreateDto.toEntity()));

  }

  // Read all Empresas

  public List<EmpresaDto> findAllEmpresas(Pageable pageable){
    return empresaRepository.findAll(pageable).stream().map(EmpresaDto::fromEntity).toList();
  }

  // Read all Empresas

  public List<EmpresaDto> findAllEmpresas(){
    return empresaRepository.findAll().stream().map(EmpresaDto::fromEntity).toList();
  }

  // Find Empresa by Id

  public Empresa findEmpresaById(Long id){
    return empresaRepository.findById(id).orElseThrow(EmpresaNotFoundException::new);
  }

  // Find Empresa By Cnpj

  public EmpresaDto findEmpresaByCnpj(String cnpj){

    if(cnpj == null){
      throw new IncorrectCnpjException();
    }

    String normalizedCnpj = cnpj
        .trim()
        .replace(".", "")
        .replace("-", "")
        .replace("/", "");

    if (normalizedCnpj.length() != 14){
      throw new IncorrectCnpjException();
    }

    if(cnpj.length() != 14){
      throw new IncorrectCnpjException();
    } else {
      return EmpresaDto.fromEntity(empresaRepository.findByCnpj(cnpj).orElseThrow(EmpresaNotFoundException::new));
    }

  }
}
