package com.base.CRM.config;

import com.base.CRM.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class SecurityFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserService userService;
  private final AccountStatusUserDetailsChecker accountStatusUserDetailsChecker = new AccountStatusUserDetailsChecker();

  public SecurityFilter(JwtService jwtService, UserService userService) {
    this.jwtService = jwtService;
    this.userService = userService;
  }
  
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String token = recoverToken(request);

    if (token == null) {
      filterChain.doFilter(request, response);
      return;
    }
    
    String email = jwtService.validateToken(token);
    
    if(email == null || email.isBlank()){
      unauthorized(response);
      return;
    }
    
    try{
      
      UserDetails user = userService.loadUserByUsername(email);
      
      accountStatusUserDetailsChecker.check(user);
      
      UsernamePasswordAuthenticationToken authentication =
          UsernamePasswordAuthenticationToken.authenticated(
              user,
              null,
              user.getAuthorities()
          );
      
      SecurityContext context = SecurityContextHolder.getContext();
      context.setAuthentication(authentication);
      SecurityContextHolder.setContext(context);
      
    } catch (AuthenticationException e){
      unauthorized(response);
      return;
    }
    
    filterChain.doFilter(request, response);
    
  }

  private String recoverToken(HttpServletRequest request) {

    String authorizationHeader = request.getHeader("Authorization");

    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      return null;
    } else {
      return authorizationHeader.substring(7);
    }

  }

  private void unauthorized(HttpServletResponse response) throws IOException {

    SecurityContextHolder.clearContext();
    
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    
    response.getWriter().write(
        "{\"message\":\"Autenticação inválida ou expirada.\"}"
    );
    
  }


}
