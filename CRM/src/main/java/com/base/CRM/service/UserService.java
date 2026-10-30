package com.base.CRM.service;

import com.base.CRM.entity.User;
import com.base.CRM.entity.dto.UserCreateDto;
import com.base.CRM.entity.dto.UserDto;
import com.base.CRM.exception.UserAlreadyRegistered;
import com.base.CRM.exception.UserNotFoundException;
import com.base.CRM.repository.UserRepository;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }
  
  
  public UserDto createUser(UserCreateDto userCreateDto){
    
    if(userRepository.findByEmail(userCreateDto.email()).isPresent()){
      throw new UserAlreadyRegistered();
    }
    
    String password = passwordEncoder.encode(userCreateDto.password());
    
    return UserDto.fromEntity(userRepository.save(userCreateDto.toEntity(password)));
    
  }
  
  
  // Find User By Id

  public User findUserById(Long userId){

    return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

  }

  // Get All Users

  public List<UserDto> findAllUsers(Pageable pageable){
    return userRepository.findAll(pageable)
        .stream()
        .map(
            UserDto::fromEntity
        ).toList();
  }

  // Delete User By Id

  public UserDto deleteUserById(Long userId){
    User user = findUserById(userId);
    user.setActive(false);
    return UserDto.fromEntity(userRepository.save(user));
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByEmail(username).orElseThrow(UserNotFoundException::new);
  }

}
