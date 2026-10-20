package com.base.CRM.repository;

import com.base.CRM.entity.Lead;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeadRepository extends JpaRepository<Lead, Long> {

  List<Lead> findByCompanyId(Long empresaId);

  Optional<Lead> findByEmail(String email);

  Optional<Lead> findByTelephone(String email);

}
