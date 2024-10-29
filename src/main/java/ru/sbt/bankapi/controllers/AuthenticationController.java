package ru.sbt.bankapi.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sbt.bankapi.dto.rq.AuthenticationRequest;
import ru.sbt.bankapi.dto.rq.RegisterRequest;
import ru.sbt.bankapi.dto.rs.AuthenticationResponse;
import ru.sbt.bankapi.exceptions.CommonBankApiException;
import ru.sbt.bankapi.services.interfaces.AuthenticationService;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationServiceImpl;

    @Operation(summary = "Зарегестрировать нового пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь зарегистрирован успешно",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = AuthenticationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Пользователь с таким email уже существует",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = CommonBankApiException.class)))
    })
    @PostMapping("/register")
    public AuthenticationResponse register(@Validated @RequestBody RegisterRequest request) {
        return authenticationServiceImpl.register(request).orElseThrow(
            () -> new CommonBankApiException("Пользователь с таким email уже существует", HttpStatus.BAD_REQUEST)
        );
    }

    @Operation(summary = "Аутентификация пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = AuthenticationResponse.class))),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/authenticate")
    public AuthenticationResponse authenticate(@Validated @RequestBody AuthenticationRequest request) {
        return authenticationServiceImpl.authenticate(request).orElseThrow(
            () -> new CommonBankApiException("Пользователь не найден", HttpStatus.NOT_FOUND));
    }
}
