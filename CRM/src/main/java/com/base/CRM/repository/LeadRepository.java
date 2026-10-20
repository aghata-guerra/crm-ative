package com.base.CRM.repository;

import com.base.CRM.entity.Lead;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeadRepository extends JpaRepository<Lead, Long> {

  List<Lead> findByCompanyId(Long empresaId);

}
