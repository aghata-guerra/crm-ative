package com.base.CRM.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.base.CRM.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

class JwtServiceTest {

  private final JwtService jwtService = new JwtService("minha-chave-secreta-de-teste-123456");

  @Test
  @DisplayName("Deve gerar um token JWT")
  void shouldGenerateJwtToken() {
    UserDetails userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn("user@test.com");

    String token = jwtService.generateJwtToken(userDetails);

    assertNotNull(token);
    assertFalse(token.isBlank());

    String subject = JWT.require(Algorithm.HMAC256("minha-chave-secreta-de-teste-123456"))
        .withIssuer("crm-ative")
        .build()
        .verify(token)
        .getSubject();

    assertEquals("user@test.com", subject);
  }

  @Test
  @DisplayName("Deve validar um token JWT valido")
  void shouldValidateValidToken() {
    UserDetails userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn("user@test.com");

    String token = jwtService.generateJwtToken(userDetails);

    String subject = jwtService.validateToken(token);

    assertEquals("user@test.com", subject);
  }

  @Test
  @DisplayName("Deve retornar null para token invalido")
  void shouldReturnNullForInvalidToken() {
    String subject = jwtService.validateToken("token.invalido.aqui");

    assertNull(subject);
  }
}
