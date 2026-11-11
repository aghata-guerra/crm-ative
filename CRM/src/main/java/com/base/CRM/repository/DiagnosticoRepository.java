package com.base.CRM.repository;

import com.base.CRM.entity.Diagnostico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosticoRepository extends JpaRepository<Diagnostico, Long> {
    org.springframework.data.domain.Page<Diagnostico> findByNegociacao_User_Id(
        Long userId, org.springframework.data.domain.Pageable pageable);


}
