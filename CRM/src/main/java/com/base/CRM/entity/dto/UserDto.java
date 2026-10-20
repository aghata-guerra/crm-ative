package com.base.CRM.entity.dto;

import com.base.CRM.entity.User;

public record UserDto(
    Long userId,
    String name,
    String email,
    Boolean active
) {

  public static UserDto fromEntity(User user){

    return new UserDto(
      user.getId(),
      user.getName(),
      user.getEmail(),
      user.getActive()
    );

  }

}
