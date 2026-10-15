package com.base.CRM.entity;

import com.base.CRM.enumClass.StatusProximoPasso;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;



@Entity
@Table(name = "proximoPasso")
public class ProximoPasso {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "negociacaoId")
  private Negociacao negociacao;

  @ManyToOne
  @JoinColumn(name = "userId")
  private User user;

  private String description;

  private LocalDate expectedDate;

  private StatusProximoPasso statusProximoPasso;

  private LocalDate finishedAt;

  private LocalDate createdAt = LocalDate.now();

  public ProximoPasso(Long id, Negociacao negociacao, User user, String description,
      LocalDate expectedDate, StatusProximoPasso statusProximoPasso) {
    this.id = id;
    this.negociacao = negociacao;
    this.user = user;
    this.description = description;
    this.expectedDate = expectedDate;
    this.statusProximoPasso = statusProximoPasso;
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public LocalDate getExpectedDate() {
    return expectedDate;
  }

  public void setExpectedDate(LocalDate expectedDate) {
    this.expectedDate = expectedDate;
  }

  public StatusProximoPasso getStatusProximoPasso() {
    return statusProximoPasso;
  }

  public void setStatusProximoPasso(StatusProximoPasso statusProximoPasso) {
    this.statusProximoPasso = statusProximoPasso;
  }

  public LocalDate getFinishedAt() {
    return finishedAt;
  }

  public void setFinishedAt(LocalDate finishedAt) {
    this.finishedAt = finishedAt;
  }

  public LocalDate getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDate createdAt) {
    this.createdAt = createdAt;
  }
}
