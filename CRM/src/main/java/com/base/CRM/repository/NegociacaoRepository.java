package com.base.CRM.repository;

import com.base.CRM.entity.Negociacao;
import com.base.CRM.enumClass.Etapa;
import com.base.CRM.enumClass.StatusNegociacao;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NegociacaoRepository extends JpaRepository<Negociacao, Long> {

  List<Negociacao> findByUser_Id(Long id);

  List<Negociacao> findByLead_Id(Long id);

  List<Negociacao> findByEtapa(Etapa etapa);

  List<Negociacao> findByUpdatedStageDateBeforeAndStatusNegociacao(LocalDate limit, StatusNegociacao statusNegociacao);
}
