package com.base.CRM.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lead")
public class Lead {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private String email;

  private String telephone;

  private String cargo;

  @Column(unique = true, updatable = false)
  private final LocalDateTime createdAt = LocalDateTime.now();

  @ManyToOne
  @JoinColumn(name = "companyId")
  private Empresa company;

  @OneToMany(mappedBy = "lead")
  private List<Negociacao> negociacaoList = new ArrayList<>();

  public Lead(Long id, String name, String email, String telephone, String cargo, Empresa company) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.telephone = telephone;
    this.cargo = cargo;
    this.company = company;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getTelephone() {
    return telephone;
  }

  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }

  public String getCargo() {
    return cargo;
  }

  public void setCargo(String cargo) {
    this.cargo = cargo;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public Empresa getCompany() {
    return company;
  }

  public void setCompany(Empresa company) {
    this.company = company;
  }

  public List<Negociacao> getNegociacaoList() {
    return negociacaoList;
  }

  public void setNegociacaoList(List<Negociacao> negociacaoList) {
    this.negociacaoList = negociacaoList;
  }
}
