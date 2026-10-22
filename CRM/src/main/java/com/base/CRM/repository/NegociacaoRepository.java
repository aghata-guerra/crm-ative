package com.base.CRM.repository;

import com.base.CRM.entity.Negociacao;
import com.base.CRM.entity.dto.NegociacoesPorOrigemDto;
import com.base.CRM.enumClass.Etapa;
import com.base.CRM.enumClass.StatusNegociacao;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NegociacaoRepository extends JpaRepository<Negociacao, Long> {

  List<Negociacao> findByUser_Id(Long id);

  List<Negociacao> findByLead_Id(Long id);

  List<Negociacao> findByStage(Etapa stage);

  List<Negociacao> findByUpdatedStageDateBeforeAndStatusNegociacao(LocalDate limit, StatusNegociacao statusNegociacao);

  List<Negociacao> findByProximoPassoIsEmpty();

  @Query("""
    SELECT new com.base.CRM.entity.dto.NegociacoesPorOrigemDto(
        n.origin,
        COUNT(n)
    )
    FROM Negociacao n
    GROUP BY n.origin
    ORDER BY COUNT(n) DESC
    """)
  List<NegociacoesPorOrigemDto> countNegociacoesPorOrigem();

}
