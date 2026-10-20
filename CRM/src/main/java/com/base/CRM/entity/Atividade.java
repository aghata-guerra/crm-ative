package com.base.CRM.entity;


import com.base.CRM.enumClass.Tipo;
import jakarta.persistence.Column;
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
import java.time.LocalDate;

@Entity
@Table(name = "atividade")
public class Atividade {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "atividadeId")
  private Negociacao negociacao;

  @ManyToOne
  @JoinColumn(name = "userId")
  private User user;

  @Enumerated(EnumType.STRING)
  private Tipo type;

  private String description;

  private LocalDate happenDate;

  @Column(updatable = false, nullable = false)
  private final LocalDate createdAt = LocalDate.now();


  public Atividade() {
  }

  public Atividade(Long id, Negociacao negociacao, User user, Tipo type, String description,
      LocalDate happenDate) {
    this.id = id;
    this.negociacao = negociacao;
    this.user = user;
    this.type = type;
    this.description = description;
    this.happenDate = happenDate;
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

  public Tipo getType() {
    return type;
  }

  public void setType(Tipo type) {
    this.type = type;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public LocalDate getHappenDate() {
    return happenDate;
  }

  public void setHappenDate(LocalDate happenDate) {
    this.happenDate = happenDate;
  }

  public LocalDate getCreatedAt() {
    return createdAt;
  }
}
