package com.base.CRM.entity;


import com.base.CRM.enumClass.Etapa;
import com.base.CRM.enumClass.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "negociacao")
public class Negociacao {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "leadId")
  private Lead lead;

  @ManyToOne
  @JoinColumn(name = "userId")
  private User user;

  private String origin;

  @Enumerated(EnumType.STRING)
  private Etapa stage;

  @Enumerated(EnumType.STRING)
  private Status status;

  private BigDecimal estimatedCost;

  private LocalDate startDate;

  private LocalDate finalDate;

  private LocalDate updatedStageDate;

  @ManyToOne
  @JoinColumn(name = "atividadeId")
  private List<Atividade> atividadeList = new ArrayList<>();

  public Negociacao(Long id, Lead lead, User user, String origin, Etapa stage, Status status,
      BigDecimal estimatedCost, LocalDate startDate, LocalDate finalDate,
      LocalDate updatedStageDate) {
    this.id = id;
    this.lead = lead;
    this.user = user;
    this.origin = origin;
    this.stage = stage;
    this.status = status;
    this.estimatedCost = estimatedCost;
    this.startDate = startDate;
    this.finalDate = finalDate;
    this.updatedStageDate = updatedStageDate;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Lead getLead() {
    return lead;
  }

  public void setLead(Lead lead) {
    this.lead = lead;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getOrigin() {
    return origin;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  public Etapa getStage() {
    return stage;
  }

  public void setStage(Etapa stage) {
    this.stage = stage;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public BigDecimal getEstimatedCost() {
    return estimatedCost;
  }

  public void setEstimatedCost(BigDecimal estimatedCost) {
    this.estimatedCost = estimatedCost;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public LocalDate getFinalDate() {
    return finalDate;
  }

  public void setFinalDate(LocalDate finalDate) {
    this.finalDate = finalDate;
  }

  public LocalDate getUpdatedStageDate() {
    return updatedStageDate;
  }

  public void setUpdatedStageDate(LocalDate updatedStageDate) {
    this.updatedStageDate = updatedStageDate;
  }
}
