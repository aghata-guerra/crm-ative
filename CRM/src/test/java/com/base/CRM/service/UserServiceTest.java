package com.base.CRM.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.base.CRM.entity.User;
import com.base.CRM.entity.dto.UserCreateDto;
import com.base.CRM.entity.dto.UserDto;
import com.base.CRM.exception.UserAlreadyRegistered;
import com.base.CRM.exception.UserNotFoundException;
import com.base.CRM.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Test
  @DisplayName("Deve criar um usuario")
  void shouldCreateUser() {
    UserCreateDto dto = new UserCreateDto("Usuário Teste", "user@test.com", "senha123");
    User user = new User();
    user.setId(1L);

    when(userRepository.findByEmail("user@test.com")).thenReturn(java.util.Optional.empty());
    when(passwordEncoder.encode("senha123")).thenReturn("senha-encriptada");
    when(userRepository.save(any(User.class))).thenReturn(user);

    UserDto result = userService.createUser(dto);

    assertNotNull(result);
    assertEquals(1L, result.userId());
  }

  @Test
  @DisplayName("Deve lancar excecao quando email ja cadastrado")
  void shouldThrowWhenEmailAlreadyRegistered() {
    UserCreateDto dto = new UserCreateDto("Usuário Teste", "user@test.com", "senha123");

    when(userRepository.findByEmail("user@test.com")).thenReturn(java.util.Optional.of(new User()));

    assertThrows(UserAlreadyRegistered.class, () -> userService.createUser(dto));
  }

  @Test
  @DisplayName("Deve retornar usuario por id")
  void shouldFindUserById() {
    User user = new User();
    user.setId(1L);

    when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));

    User result = userService.findUserById(1L);

    assertNotNull(result);
    assertEquals(1L, result.getId());
  }

  @Test
  @DisplayName("Deve lancar excecao quando usuario nao encontrado")
  void shouldThrowWhenUserNotFound() {
    when(userRepository.findById(1L)).thenReturn(java.util.Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.findUserById(1L));
  }

  @Test
  @DisplayName("Deve listar todos usuarios paginados")
  void shouldFindAllUsersPaginated() {
    User user = new User();
    user.setId(1L);
    Page<User> page = new PageImpl<>(List.of(user));

    when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

    List<UserDto> result = userService.findAllUsers(org.springframework.data.domain.PageRequest.of(0, 10));

    assertEquals(1, result.size());
  }

  @Test
  @DisplayName("Deve deletar usuario por id (soft delete)")
  void shouldDeleteUserById() {
    User user = new User();
    user.setId(1L);
    user.setActive(true);

    when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
    when(userRepository.save(any(User.class))).thenReturn(user);

    UserDto result = userService.deleteUserById(1L);

    assertNotNull(result);
    assertFalse(user.getActive());
  }

  @Test
  @DisplayName("Deve carregar usuario por username")
  void shouldLoadUserByUsername() {
    User user = new User();
    user.setEmail("user@test.com");
    user.setActive(true);

    when(userRepository.findByEmail("user@test.com")).thenReturn(java.util.Optional.of(user));

    UserDetails result = userService.loadUserByUsername("user@test.com");

    assertNotNull(result);
    assertEquals("user@test.com", result.getUsername());
  }

  @Test
  @DisplayName("Deve lancar excecao ao carregar usuario inexistente")
  void shouldThrowWhenLoadUserByUsernameNotFound() {
    when(userRepository.findByEmail("naoexiste@test.com")).thenReturn(java.util.Optional.empty());

    assertThrows(UserNotFoundException.class,
        () -> userService.loadUserByUsername("naoexiste@test.com"));
  }
}
