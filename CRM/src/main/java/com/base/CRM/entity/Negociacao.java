package com.base.CRM.entity;


import com.base.CRM.enumClass.Etapa;
import com.base.CRM.enumClass.StatusNegociacao;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
  private StatusNegociacao statusNegociacao;

  private BigDecimal estimatedCost;

  private LocalDate startDate = LocalDate.now();

  private LocalDate finalDate;

  private LocalDate updatedStageDate;

  @OneToMany(mappedBy = "negociacao", cascade = CascadeType.ALL)
  private List<Atividade> atividadeList = new ArrayList<>();

  @OneToMany(mappedBy = "negociacao", cascade = CascadeType.ALL)
  private List<ProximoPasso> proximoPasso = new ArrayList<>();

  @OneToMany(mappedBy = "negociacao", cascade = CascadeType.ALL)
  private List<Diagnostico> diagnosticoList = new ArrayList<>();

  public Negociacao(Long id, Lead lead, User user, String origin,
      Etapa stage, StatusNegociacao statusNegociacao, BigDecimal estimatedCost) {
    this.id = id;
    this.lead = lead;
    this.user = user;
    this.origin = origin;
    this.stage = stage;
    this.statusNegociacao = statusNegociacao;
    this.estimatedCost = estimatedCost;
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

  public StatusNegociacao getStatus() {
    return statusNegociacao;
  }

  public void setStatus(StatusNegociacao statusNegociacao) {
    this.statusNegociacao = statusNegociacao;
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

  public List<Atividade> getAtividadeList() {
    return atividadeList;
  }

  public void setAtividadeList(List<Atividade> atividadeList) {
    this.atividadeList = atividadeList;
  }

  public List<ProximoPasso> getProximoPasso() {
    return proximoPasso;
  }

  public void setProximoPasso(List<ProximoPasso> proximoPasso) {
    this.proximoPasso = proximoPasso;
  }

  public StatusNegociacao getStatusNegociacao() {
    return statusNegociacao;
  }

  public void setStatusNegociacao(StatusNegociacao statusNegociacao) {
    this.statusNegociacao = statusNegociacao;
  }

  public List<Diagnostico> getDiagnosticoList() {
    return diagnosticoList;
  }

  public void setDiagnosticoList(List<Diagnostico> diagnosticoList) {
    this.diagnosticoList = diagnosticoList;
  }
}
