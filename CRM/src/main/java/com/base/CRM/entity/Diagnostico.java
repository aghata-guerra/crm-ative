package com.base.CRM.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "diagnostico")
public class Diagnostico {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "negociacaoId")
  private Negociacao negociacao;

  @ManyToOne
  @JoinColumn(name = "userId")
  private User user;

  private Integer companySize;

  private String currentSituation;

  private String mainDifficulty;

  private String needs;

  private LocalDate updatedAt;

  public Diagnostico(Long id, Negociacao negociacao, User user, Integer companySize,
      String currentSituation, String mainDifficulty, String needs, LocalDate updatedAt) {
    this.id = id;
    this.negociacao = negociacao;
    this.user = user;
    this.companySize = companySize;
    this.currentSituation = currentSituation;
    this.mainDifficulty = mainDifficulty;
    this.needs = needs;
    this.updatedAt = updatedAt;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Negociacao getNegociacao() {
    return negociacao;
  }

  public void setNegociacao(Negociacao negociacao) {
    this.negociacao = negociacao;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Integer getCompanySize() {
    return companySize;
  }

  public void setCompanySize(Integer companySize) {
    this.companySize = companySize;
  }

  public String getCurrentSituation() {
    return currentSituation;
  }

  public void setCurrentSituation(String currentSituation) {
    this.currentSituation = currentSituation;
  }

  public String getMainDifficulty() {
    return mainDifficulty;
  }

  public void setMainDifficulty(String mainDifficulty) {
    this.mainDifficulty = mainDifficulty;
  }

  public String getNeeds() {
    return needs;
  }

  public void setNeeds(String needs) {
    this.needs = needs;
  }

  public LocalDate getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDate updatedAt) {
    this.updatedAt = updatedAt;
  }
}
