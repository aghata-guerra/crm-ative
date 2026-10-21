package com.base.CRM.entity.dto;

import com.base.CRM.entity.User;
import com.base.CRM.enumClass.Profile;

public record UserDto(
    Long userId,
    String name,
    String email,
    Profile profile,
    Boolean active
) {

  public static UserDto fromEntity(User user){

    return new UserDto(
      user.getId(),
      user.getName(),
      user.getEmail(),
      user.getProfile(),
      user.getActive()
    );

  }

}
