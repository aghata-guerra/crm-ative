package com.base.CRM.repository;

import com.base.CRM.entity.Empresa;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

  Optional<Empresa> findByCnpj(String cnpj);

}
