package ru.sbt.bankapi.dto.rs;


import ru.sbt.bankapi.models.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String token;
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private Role role;
}
