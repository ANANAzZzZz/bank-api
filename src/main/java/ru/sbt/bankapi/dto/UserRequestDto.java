package ru.sbt.bankapi.dto;

import ru.sbt.bankapi.models.Role;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDto(
    Long id,
    @NotBlank
    String firstname,
    @NotBlank
    String lastname,
    @NotBlank
    String password,
    @NotBlank
    String email,
    Role role
) {
}
