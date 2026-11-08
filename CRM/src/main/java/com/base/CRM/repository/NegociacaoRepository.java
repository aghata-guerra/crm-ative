package com.base.CRM.repository;

import com.base.CRM.entity.Negociacao;
import com.base.CRM.entity.dto.NegociacoesPorOrigemDto;
import com.base.CRM.enumClass.Etapa;
import com.base.CRM.enumClass.StatusNegociacao;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NegociacaoRepository extends JpaRepository<Negociacao, Long> {

  List<Negociacao> findByUser_Id(Long id);

  List<Negociacao> findByLead_Id(Long id);

  List<Negociacao> findByStage(Etapa stage);

  Page<Negociacao> findByUpdatedStageDateBeforeAndStatusNegociacao(LocalDate limit, StatusNegociacao statusNegociacao, Pageable pageable);

  Page<Negociacao> findByUpdatedStageDateBeforeAndStatusNegociacaoAndUser_Id(LocalDate limit, StatusNegociacao statusNegociacao, Long userId, Pageable pageable);
  
  List<Negociacao> findByProximoPassoIsEmpty();
  
  List<Negociacao> findByProximoPassoIsEmptyAndUser_Id(Long id);
  
  List<Negociacao> findByEtapaAndStatusNegociacaoAndUser_Id(Etapa etapa, StatusNegociacao statusNegociacao, Long id);

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

  @Query("""
    SELECT n FROM Negociacao n
    WHERE (:stage IS NULL OR n.stage = :stage)
      AND (:status IS NULL OR n.statusNegociacao = :status)
    """)
  Page<Negociacao> findByFilters(
      @Param("stage") Etapa stage,
      @Param("status") StatusNegociacao status,
      Pageable pageable
  );

  @Query("""
    SELECT n FROM Negociacao n
    WHERE n.responsavel.id = :usuarioId
      AND (:stage IS NULL OR n.stage = :stage)
      AND (:status IS NULL OR n.statusNegociacao = :status)
    """)
  Page<Negociacao> findByFiltersAndResponsavel(
      @Param("usuarioId") Long usuarioId,
      @Param("stage") Etapa stage,
      @Param("status") StatusNegociacao status,
      Pageable pageable
  );

}
