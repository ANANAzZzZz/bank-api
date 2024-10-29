package ru.sbt.bankapi.dto.rq;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank
    @Size(min = 1, max = 255)
    private String firstname;
    @NotBlank
    @Size(min = 1, max = 255)
    private String lastname;
    @NotBlank
    @Size(min = 5, max = 255)
    @Email
    private String email;
    @NotBlank
    @Size(min = 5, max = 255)
    private String password;
}
