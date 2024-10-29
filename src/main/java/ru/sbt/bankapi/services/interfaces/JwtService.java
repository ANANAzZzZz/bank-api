package ru.sbt.bankapi.services.interfaces;

import org.springframework.security.core.userdetails.UserDetails;
import ru.sbt.bankapi.models.User;

import java.util.Optional;

public interface JwtService {
    String extractUsername(String token);

    String generateToken(UserDetails userDetails);

    String generateToken(
        java.util.Map<String, Object> extraClaims,
        UserDetails userDetails
    );

    boolean isTokenValid(String token, UserDetails userDetails);

    Optional<User> findUserByEmail(String email);
}