package com.base.CRM.repository;

import com.base.CRM.entity.Atividade;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtividadeRepository extends JpaRepository<Atividade, Long> {

  List<Atividade> findAllByNegociacaoId(Long negociacaoId);

}
