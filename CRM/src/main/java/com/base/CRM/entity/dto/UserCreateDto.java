
package com.base.CRM.entity.dto;

import jakarta.validation.constraints.*;

import com.base.CRM.entity.User;
import com.base.CRM.enumClass.Profile;

public record UserCreateDto(
    @NotBlank @Size(max=100) String name,
    @NotBlank @Email String email,
    @NotBlank @Size(min=8, max=72) String password
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
