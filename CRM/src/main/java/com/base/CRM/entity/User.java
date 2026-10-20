package com.base.CRM.entity;

import com.base.CRM.enumClass.Profile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "user")
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @Column(unique = true)
  private String email;

  private String password;

  @Enumerated(EnumType.STRING)
  private Profile profile;

  private Boolean active;

  @OneToMany(mappedBy = "user")
  private List<Negociacao> negociacaoList = new ArrayList<>();

  @OneToMany(mappedBy = "user")
  private List<Atividade> atividadeList = new ArrayList<>();

  @OneToMany(mappedBy = "user")
  private List<ProximoPasso> proximoPassoList = new ArrayList<>();

  @OneToMany(mappedBy = "user")
  private List<Diagnostico> diagnosticoList = new ArrayList<>();

  public User() {
  }

  public User(Long id, String name, String email, String password, Profile profile,
      Boolean active) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.password = password;
    this.profile = profile;
    this.active = active;
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

  public void setPassword(String password) {
    this.password = password;
  }

  public Profile getProfile() {
    return profile;
  }

  public void setProfile(Profile profile) {
    this.profile = profile;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public List<Negociacao> getNegociacaoList() {
    return negociacaoList;
  }

  public void setNegociacaoList(List<Negociacao> negociacaoList) {
    this.negociacaoList = negociacaoList;
  }

  public List<Atividade> getAtividadeList() {
    return atividadeList;
  }

  public void setAtividadeList(List<Atividade> atividadeList) {
    this.atividadeList = atividadeList;
  }

  public List<ProximoPasso> getProximoPassoList() {
    return proximoPassoList;
  }

  public void setProximoPassoList(List<ProximoPasso> proximoPassoList) {
    this.proximoPassoList = proximoPassoList;
  }

  public List<Diagnostico> getDiagnosticoList() {
    return diagnosticoList;
  }

  public void setDiagnosticoList(List<Diagnostico> diagnosticoList) {
    this.diagnosticoList = diagnosticoList;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_"+profile.name()));
  }

  @Override
  public @Nullable String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return active;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
