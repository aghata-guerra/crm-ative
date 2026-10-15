package com.base.CRM.entity;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "empresa")
public class Empresa {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String companyName;

  private String tradeName;

  @Column(unique = true)
  private String cnpj;

  private String telephone;

  @Column(nullable = false, updatable = false)
  private final LocalDateTime createdAt = LocalDateTime.now();

  @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL)
  private List<Lead> leadList = new ArrayList<>();

  public Empresa(String telephone, String cnpj, String tradeName, String companyName, Long id) {
    this.telephone = telephone;
    this.cnpj = cnpj;
    this.tradeName = tradeName;
    this.companyName = companyName;
    this.id = id;
  }

  public List<Lead> getLeadList() {
    return leadList;
  }

  public void setLeadList(List<Lead> leadList) {
    this.leadList = leadList;
  }

  public String getCnpj() {
    return cnpj;
  }

  public void setCnpj(String cnpj) {
    this.cnpj = cnpj;
  }

  public String getTelephone() {
    return telephone;
  }

  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }

  public String getTradeName() {
    return tradeName;
  }

  public void setTradeName(String tradeName) {
    this.tradeName = tradeName;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
