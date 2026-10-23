package com.base.CRM.repository;

import com.base.CRM.entity.ProximoPasso;
import com.base.CRM.enumClass.StatusProximoPasso;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProximoPassoRepository extends JpaRepository<ProximoPasso, Long> {

  List<ProximoPasso> findByNegociacaoId(Long negociacaoId);

  @Query("SELECT p FROM ProximoPasso p WHERE p.expectedDate BETWEEN :startDate AND :endDate AND p.statusProximoPasso = :status")
  List<ProximoPasso> findByExpectedDateBetweenAndStatus(
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      @Param("status") StatusProximoPasso status
  );

  List<ProximoPasso> findByUserId(Long userId);
}
