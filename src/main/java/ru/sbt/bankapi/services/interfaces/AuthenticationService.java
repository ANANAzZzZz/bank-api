package ru.sbt.bankapi.services.interfaces;

import ru.sbt.bankapi.dto.rq.AuthenticationRequest;
import ru.sbt.bankapi.dto.rq.RegisterRequest;
import ru.sbt.bankapi.dto.rs.AuthenticationResponse;

import java.util.Optional;

public interface AuthenticationService {
    Optional<AuthenticationResponse> register(RegisterRequest request);

    Optional<AuthenticationResponse> authenticate(AuthenticationRequest request);
}

