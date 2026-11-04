
package com.base.CRM.entity.dto;

import com.base.CRM.entity.User;
import com.base.CRM.enumClass.Profile;

public record UserCreateDto(
    String name,
    String email,
    String password
) {

    public User toEntity(String encodedPassword) {
        return new User(
            null,
            name,
            email,
            encodedPassword,
            Profile.VENDEDOR,
            true
        );
    }
}
