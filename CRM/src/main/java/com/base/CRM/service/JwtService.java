package com.base.CRM.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  
  private final String secret_key;

  public JwtService(
      @Value("${api.security.token.secret}") String secret_key) {
    this.secret_key = secret_key;
  }

  public String generateJwtToken(UserDetails userDetails){

    Algorithm algorithm = Algorithm.HMAC256(secret_key);

    return JWT.create()
        .withIssuer("crm-ative")
        .withSubject(userDetails.getUsername())
        .withIssuedAt(Instant.now())
        .withExpiresAt(
            Instant.now().plus(2, ChronoUnit.HOURS)
        )
        .sign(algorithm);
  }

  public String validateToken(String token){

    try {

      Algorithm algorithm = Algorithm.HMAC256(secret_key);

      return JWT.require(algorithm)
          .withIssuer("crm-ative")
          .build()
          .verify(token)
          .getSubject();
    } catch (JWTVerificationException exception){
      return null;
    }

  }


}
