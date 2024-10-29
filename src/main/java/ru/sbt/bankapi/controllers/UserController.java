package ru.sbt.bankapi.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sbt.bankapi.dto.UserRequestDto;
import ru.sbt.bankapi.dto.UserResponseDto;
import ru.sbt.bankapi.exceptions.CommonBankApiException;
import ru.sbt.bankapi.services.interfaces.UserService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userServiceImpl;

    @Operation(summary = "Получить всех пользователей")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно"),
        @ApiResponse(responseCode = "404", description = "Пользователи не найдены")
    })
    @GetMapping()
    public List<UserResponseDto> getUsers() {
        return userServiceImpl.getAllUsers();
    }

    @Operation(summary = "Обновить пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно"),
        @ApiResponse(responseCode = "400", description = "Неверные учетные данные")
    })
    @PutMapping()
    public UserResponseDto updateUser(@Validated @RequestBody UserRequestDto userRequestDto) {
        return userServiceImpl.updateUser(userRequestDto).orElseThrow(
            () -> new CommonBankApiException("Пользователь не найден", HttpStatus.BAD_REQUEST)
        );
    }

    @Operation(summary = "Получить пользователя по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{id}")
    public UserRequestDto getUserById(@PathVariable Long id) {
        return userServiceImpl.findUserById(id).orElseThrow(
            () -> new CommonBankApiException("Пользователь не найден", HttpStatus.NOT_FOUND)
        );
    }

    @Operation(summary = "Удалить пользователя по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        return userServiceImpl.deleteUser(userId);
    }
}
