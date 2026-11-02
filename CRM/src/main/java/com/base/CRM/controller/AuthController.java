package com.base.CRM.controller;

import com.base.CRM.service.JwtService;
import com.base.CRM.entity.User;
import com.base.CRM.entity.dto.LoginRequestDto;
import com.base.CRM.entity.dto.LoginResponseDto;
import com.base.CRM.entity.dto.UserCreateDto;
import com.base.CRM.entity.dto.UserDto;
import com.base.CRM.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthController {
  
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserService userService;

  public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
      UserService userService) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.userService = userService;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto){

    Authentication authentication = authenticationManager.authenticate(
        UsernamePasswordAuthenticationToken.unauthenticated(
            loginRequestDto.email(),
            loginRequestDto.password()
        )
    );

    User user = (User) authentication.getPrincipal();

    String token = jwtService.generateJwtToken(user);
    
    LoginResponseDto loginResponseDto = new LoginResponseDto(
        token,
        "Bearer",
        7200L
    );
    
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(loginResponseDto);
    
  }
  
  @PostMapping("/register")
  public ResponseEntity<UserDto> register(@RequestBody UserCreateDto userCreateDto){
    
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(userService.createUser(userCreateDto));
  }
  
  
}
